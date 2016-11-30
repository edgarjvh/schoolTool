package com.example.school.toolinfodoc;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;

public class Principal extends AppCompatActivity {

    Object response = null;
    String mensaje = "";
    lvCalendarioItems CalendarioItems[];
    private Representante representante;
    private ListView lvCalendario;
    private CustomProgress dialogMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        lvCalendario = (ListView)findViewById(R.id.lvCalendario);
        RadioGroup rgroupOpcionesCalendario = (RadioGroup) findViewById(R.id.rgroupOpcionesCalendario);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            representante = (Representante) getIntent().getSerializableExtra("Representante"); //Obtaining data
        }

        AutoResizeTextView lblNombre = (AutoResizeTextView)findViewById(R.id.lblNombre);
        String nombre = "Bienvenido(a)\n" + "<font color='#0808e1'>" + representante.getApellidos() + ", " + representante.getNombres() + "</font>";
        lblNombre.setText(Html.fromHtml(nombre), TextView.BufferType.SPANNABLE);

        Resources res = getResources();

        TabHost tabs=(TabHost)findViewById(R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("calendario");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",
                res.getDrawable(R.drawable.calendar_icon));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mensajes");
        spec.setContent(R.id.tab2);

        spec.setIndicator("",
                res.getDrawable(R.drawable.message_icon));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        rgroupOpcionesCalendario.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbtn = (RadioButton)radioGroup.findViewById(i);
                // This puts the value (true/false) into the variable

                Log.d("radio",rbtn.getText().toString());
                int value = 0;

                switch (rbtn.getText().toString()){
                    case "Esta Semana" :
                        mensaje = "Buscando eventos para esta semana. Por favor espere...";
                        value = 0;
                        break;
                    case "Este Mes" :
                        mensaje = "Buscando eventos para este mes. Por favor espere...";
                        value = 1;
                        break;
                    case "Este Año" :
                        mensaje = "Buscando eventos para este año. Por favor espere...";
                        value = 2;
                        break;
                }

                lvCalendario.setAdapter(null);
                new AsyncCalendario().execute(representante.getId(),value);
            }
        });

        mensaje = "Buscando eventos para esta semana. Por favor espere...";
        new AsyncCalendario().execute(representante.getId(), 0);
    }

    private class AsyncCalendario extends AsyncTask<Object, Integer, Integer> {
        @Override
        protected Integer doInBackground(Object... params) {
            publishProgress(0);
            ArrayList<Object>  parametros = new ArrayList<>(4);
            parametros.add(0, "Id*" + params[0]);
            parametros.add(1, "TipoCalendario*"+ params[1]);
            parametros.add(2, "getCalendario");

            respuesta ws = new respuesta();
            response = ws.getData(parametros);

            Log.d("EJVH", response.getClass().toString());
            Log.d("EJVH", response.toString());

            try {
                JSONObject jsonObj = new JSONObject(response.toString());

                String result = jsonObj.get("Result").toString();

                if (result.equals("OK")){
                    JSONArray array= jsonObj.getJSONArray("Calendario");
                    CalendarioItems = new lvCalendarioItems[array.length()];

                    for (int i = 0;i < array.length();i++){
                        JSONObject evento = array.getJSONObject(i);
                        CalendarioItems[i] = new lvCalendarioItems(R.drawable.bulb_on,evento.getString("Titulo"),evento.getString("Fecha"));
                    }
                    publishProgress(1);
                }else if (result.equals("NO ROWS")){
                    mensaje = jsonObj.get("Message").toString();
                    publishProgress(2);
                }else{
                    mensaje = jsonObj.get("Message").toString();
                    publishProgress(3);
                }
                return null;
            } catch (JSONException e) {
                mensaje = e.getMessage();
                publishProgress(4);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            switch (values[0]){
                case 0:
                    mostrarMensaje(true, 0, mensaje);
                    break;
                case 1:
                    lvCalendarioItemsArrayAdapter adapter = new lvCalendarioItemsArrayAdapter(Principal.this,R.layout.lvcalendarioitemlayout,CalendarioItems);
                    lvCalendario.setAdapter(adapter);
                    if (dialogMessage != null){
                        dialogMessage.dismiss();
                        dialogMessage = null;
                    }
                    break;
                case 2 :
                    mostrarMensaje(false,0,mensaje);
                    break;
                default:
                    mostrarMensaje(false, 2, mensaje);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

    private class respuesta {
        Object getData(ArrayList<Object> parametros){
            Object data;
            String namespace = "http://schooltool.org/";
            String direccion = "http://cctv.zuprevencion.org:9650/schooltoolws.asmx";
            String metodo = parametros.get(parametros.size() - 1).toString();
            String soapAction = namespace + metodo;

            SoapObject request = new SoapObject(namespace, metodo);

            if (parametros.size() > 0){
                String property[];
                PropertyInfo pi;

                property = parametros.get(0).toString().split("\\*");
                pi = new PropertyInfo();
                pi.setName(property[0]);
                pi.setValue(property[1]);
                pi.setType(Integer.class);
                request.addProperty(pi);

                property = parametros.get(1).toString().split("\\*");
                pi = new PropertyInfo();
                pi.setName(property[0]);
                pi.setValue(property[1]);
                pi.setType(Integer.class);
                request.addProperty(pi);
            }

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(direccion);

            try {
                httpTransport.call(soapAction, envelope);
                data = envelope.getResponse();
            } catch (Exception exception) {
                data = exception.toString();
            }

            return data;
        }
    }

    private void mostrarMensaje(Boolean enProgreso,int icono, String msj){
        if(enProgreso){
            if(dialogMessage != null){
                dialogMessage.dismiss();
                dialogMessage = null;
            }
            dialogMessage = new CustomProgress(Principal.this,Principal.this,enProgreso,icono,msj);
            dialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogMessage.setCanceledOnTouchOutside(true);
            dialogMessage.show();
        }else{
            if(dialogMessage != null){
                dialogMessage.dismiss();
                dialogMessage = null;
            }

            dialogMessage = new CustomProgress(Principal.this,Principal.this,enProgreso,icono,msj);
            dialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogMessage.setCanceledOnTouchOutside(true);
            dialogMessage.show();
            CountDownTimer timer = new CountDownTimer(3000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    if(dialogMessage != null){
                        dialogMessage.dismiss();
                        dialogMessage = null;
                    }
                }
            };
            timer.start();
        }
    }
}
