package clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.io.IOException;

public class RegistrarGCM {
    private GoogleCloudMessaging GCM;
    private SharedPreferences sPrefs;
    private static final String PREF_NAME = "prefSchoolTool";
    private static final String PROPERTY_USER_TYPE = "userType"; //doc ; rep
    private static final String PROPERTY_USER = "user";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final int EXPIRATION_TIME_MS = 1000*3600*24*7;
    private static final String SENDER_ID = "694700091939";
    private static final String SERVER_API = "AIzaSyDSQz-z1HDEjO5va_RTTy2_91E1CzoLmm8";
    private Context context;
    private String tipoUsuario;
    private Representante representante;
    private Docente docente;

    public RegistrarGCM(Context context, String tipoUsuario, Object usuario){
        this.context = context;
        this.tipoUsuario = tipoUsuario;

        if (tipoUsuario.equals("doc")){
            docente = (Docente)usuario;
        }else if (tipoUsuario.equals("rep")){
            representante = (Representante)usuario;
        }

        this.sPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        registrarGcm();
    }

    // ======================== SE INICIA EL PROCESO DE REGISTRO ================
    private void registrarGcm(){
        GCM = GoogleCloudMessaging.getInstance(context);
        String regID = obtenerRegistroGcm(context);

        if (regID.equals("")){
            new AsyncRegistrarGcm().execute();
        }
    }

    // ========================= SE VALIDA SI EL USUARIO YA SE HA REGISTRADO EN ESTE DISPOSITIVO
    private String obtenerRegistroGcm(Context context){
        String prefs_userType = sPrefs.getString(PROPERTY_USER_TYPE,"");
        String prefs_regID = sPrefs.getString(PROPERTY_REG_ID,"");
        int prefs_version = sPrefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        Long prefs_expiration = sPrefs.getLong(PROPERTY_EXPIRATION_TIME, -1);

        if(prefs_userType.equals("") || prefs_regID.equals("")){
            return "";
        }else if(prefs_version != currentVersion){ // si es una version distinta
            return "";
        }else if (System.currentTimeMillis() > prefs_expiration){ // si la fecha de expiracion es menor a la fecha actual
            return "";
        }else{
            Gson gson = new Gson();

            if (prefs_userType.equals("doc") & prefs_userType.equals(tipoUsuario)){
                Docente _docente = gson.fromJson(sPrefs.getString(PROPERTY_USER,""),Docente.class);

                if (docente.getId() == _docente.getId()){
                    return prefs_regID;
                }else{
                    return "";
                }

            }else if (prefs_userType.equals("rep") & prefs_userType.equals(tipoUsuario)){
                Representante _representante = gson.fromJson(sPrefs.getString(PROPERTY_USER,""),Representante.class);

                if (representante.getId() == _representante.getId()){
                    return prefs_regID;
                }else{
                    return "";
                }
            }
            return prefs_regID;
        }
    }

    // ======================== PROCEDIMIENTO ASINCRONO DE REGISTRO
    private class AsyncRegistrarGcm extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... params) {
            try{
                if(GCM == null){
                    GCM = GoogleCloudMessaging.getInstance(context);
                }

                String regID = GCM.register(SENDER_ID);

                Boolean registrado = registroServidor(regID);

                //Guardamos los datos del registro
                if(registrado)
                {
                    setRegistrationId(context, regID);
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
            return null;
        }
    }

    private boolean registroServidor(String regId){
        boolean reg = false;

        final String NAMESPACE = "http://schooltool.org/";
        final String URL= "http://154.42.65.212:9600/schooltool.asmx";
        final String METHOD_NAME = "SaveGcmId";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("tipo", tipoUsuario.equals("doc") ? 0 : 1);
        request.addProperty("id", tipoUsuario.equals("doc") ? docente.getId() : representante.getId());
        request.addProperty("regID", regId);
        request.addProperty("apiServidor", SERVER_API);
        request.addProperty("dispositivo", getDeviceName());
        request.addProperty("versionAndroid", android.os.Build.VERSION.RELEASE);
        request.addProperty("versionApp", getAppVersion(context));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            transporte.call(SOAP_ACTION, envelope);
            SoapPrimitive response =(SoapPrimitive)envelope.getResponse();
            JSONObject jsonObj = new JSONObject(response.toString());
            String result = jsonObj.get("Result").toString();

            reg = result.equals("OK");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return reg;
    }

    private int getAppVersion(Context context){
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

//        String phrase = "";
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
//                phrase += Character.toUpperCase(c);
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
//            phrase += c;
            phrase.append(c);
        }

        return phrase.toString();
    }

    private void setRegistrationId(Context context, String regId){
        if (sPrefs == null){
            sPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }

        int appVersion = getAppVersion(context);
        Gson gson = new Gson();
        SharedPreferences.Editor sEditor = sPrefs.edit();
        sEditor.putString(PROPERTY_USER_TYPE,tipoUsuario);
        sEditor.putString(PROPERTY_USER, tipoUsuario.equals("doc") ? gson.toJson(docente) : gson.toJson(representante));
        sEditor.putString(PROPERTY_REG_ID, regId);
        sEditor.putInt(PROPERTY_APP_VERSION, appVersion);
        sEditor.putLong(PROPERTY_EXPIRATION_TIME, System.currentTimeMillis() + EXPIRATION_TIME_MS);
        sEditor.apply();
    }
}