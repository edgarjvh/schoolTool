package com.example.school.toolinfodoc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import clases.Docente;
import clases.Representante;
import controles.AutoResizeTextView;
import vistas.CustomProgress;
import vistas.SpinnerItems;
import vistas.SpinnerItemsArrayAdapter;
import vistas.lvCalendarioItems;
import vistas.lvCalendarioItemsArrayAdapter;
import vistas.lvMensajesItems;
import vistas.lvMensajesItemsArrayAdapter;

import static com.example.school.toolinfodoc.R.id.lblIdDocente;
import static com.example.school.toolinfodoc.R.id.rgroupTipoUsuario;
import static com.example.school.toolinfodoc.R.id.time;

public class Principal extends AppCompatActivity {

    Object response = null;
    String mensaje = "";
    lvCalendarioItems CalendarioItems[];
    ArrayList<lvMensajesItems> MensajesItems;
    ArrayList<SpinnerItems> spinnerItems;
    private Representante representante;
    private Docente docente;
    private String tipoUsuario;
    private ListView lvCalendario;
    private ListView lvMenu;
    private ListView lvMensajes;
    private CustomProgress dialogMessage;
    DrawerLayout drawerLayout;
    AutoResizeTextView lblSinEventos;
    int value = 0;
    View sinEventos;
    lvMensajesItemsArrayAdapter adapter;
    SpinnerItemsArrayAdapter spinnerAdapter;
    Spinner cboDocentes;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        MensajesItems = new ArrayList<>();
        spinnerItems = new ArrayList<>();

        lvCalendario = (ListView)findViewById(R.id.lvCalendario);
        lvMensajes = (ListView)findViewById(R.id.lvMensajes);
        lvMenu = (ListView)findViewById(R.id.lvMenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        RadioGroup rgroupOpcionesCalendario = (RadioGroup) findViewById(R.id.rgroupOpcionesCalendario);
        Button btnMenu = (Button)findViewById(R.id.btnMenu);
        Button btnEnviarMensaje = (Button)findViewById(R.id.btnEnviarMensaje);
        final EditText txtMensaje = (EditText)findViewById(R.id.txtMensaje);
        cboDocentes = (Spinner) findViewById(R.id.cboDocentes);

        cboDocentes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timer.cancel();
                timer.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                timer.cancel();
            }
        });

        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MensajesItems.add(new lvMensajesItems(
                        MensajesItems.size() + 1,
                        1,
                        1,
                        Calendar.getInstance().getTimeInMillis(),
                        txtMensaje.getText().toString()
                ));

                lvMensajes.setSelection(MensajesItems.size() -1);
                txtMensaje.setText(null);
                ocultarTeclado();
            }
        });

        lvCalendario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView descripcion = (TextView)view.findViewById(R.id.lblDescripcion);
                mostrarMensaje(false,1,descripcion.getText().toString());
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarOcultarMenu();
            }
        });

        Bundle extras = getIntent().getExtras();
        AutoResizeTextView lblNombre = (AutoResizeTextView)findViewById(R.id.lblNombre);

        if (extras != null) {
            tipoUsuario = getIntent().getStringExtra("TipoUsuario");

            if (tipoUsuario.equals("DOCENTE")){
                docente = (Docente) getIntent().getSerializableExtra("Docente"); //Obtaining data

                String nombre = "Bienvenido(a)\n" + "<font color='#0808e1'>" + docente.getApellidos() + ", " + docente.getNombres() + "</font>";
                lblNombre.setText(Html.fromHtml(nombre), TextView.BufferType.SPANNABLE);
            }else{
                representante = (Representante) getIntent().getSerializableExtra("Representante"); //Obtaining data

                String nombre = "Bienvenido(a)\n" + "<font color='#0808e1'>" + representante.getApellidos() + ", " + representante.getNombres() + "</font>";
                lblNombre.setText(Html.fromHtml(nombre), TextView.BufferType.SPANNABLE);
            }
        }

        lblNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(lvMenu)){
                    drawerLayout.closeDrawers();
                }
            }
        });

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

        sinEventos = findViewById(R.id.emptyView);
        lblSinEventos = (AutoResizeTextView) sinEventos.findViewById(R.id.lblSinEventos);
        lvCalendario.setEmptyView(sinEventos);

        rgroupOpcionesCalendario.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbtn = (RadioButton)radioGroup.findViewById(i);
                // This puts the value (true/false) into the variable

                Log.d("radio",rbtn.getText().toString());

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

                new AsyncCalendario().execute(representante.getId(),value);

            }
        });

        timer = new CountDownTimer(30000,100) {
            @Override
            public void onTick(long l) {
                if (cboDocentes.getSelectedView() != null){
                    int lblIdDocente = Integer.parseInt (((TextView)cboDocentes.getSelectedView().findViewById(R.id.lblIdDocente)).getText().toString());
                    new AsyncMensajes().execute(representante.getId(),lblIdDocente,0,0);
                    timer.cancel();
                }
            }

            @Override
            public void onFinish() {

            }
        };

        // crear bloque para docente y representante
        mensaje = "Buscando eventos para esta semana. Por favor espere...";
        new AsyncCalendario().execute(representante.getId(), 0);
        //new AsyncMensajes().execute(representante.getId(),0,0);
        new AsyncDocentes().execute(representante.getId());
    }

    private void ocultarTeclado(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(lvMenu)){
            mostrarOcultarMenu();
        }else{
            moveTaskToBack(true);
        }
    }

    public void mostrarOcultarMenu(){
        if (drawerLayout.isDrawerOpen(lvMenu)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(lvMenu);
        }
    }

    private class AsyncDocentes extends AsyncTask<Object,Integer, Integer>{

        @Override
        protected Integer doInBackground(Object... params) {
            publishProgress(0);
            ArrayList<Object>  parametros = new ArrayList<>(2);
            parametros.add(0, "idRepresentante*" + params[0]);
            parametros.add(1, "getDocentes");

            respuesta ws = new respuesta();
            response = ws.getData(parametros);

            Log.d("EJVH DOC", response.getClass().toString());
            Log.d("EJVH DOC", response.toString());

            try
            {
                JSONObject jsonObj = new JSONObject(response.toString());

                String result = jsonObj.get("Result").toString();

                switch (result) {
                    case "OK":
                        JSONArray array = jsonObj.getJSONArray("Docentes");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docente = array.getJSONObject(i);

                            Log.d("EJVH idDocente", Integer.toString(docente.getInt("Id")));

                            spinnerItems.add(new SpinnerItems(
                                    docente.getInt("Id"),
                                    docente.getString("Apellidos"),
                                    docente.getString("Nombres")
                            ));
                        }
                        publishProgress(1);
                        break;
                    case "NO ROWS":
                        mensaje = jsonObj.get("Message").toString();
                        publishProgress(2);
                        break;
                    default:
                        mensaje = jsonObj.get("Message").toString();
                        publishProgress(3);
                        break;
                }
                return null;
            }
            catch (JSONException e) {
                mensaje = e.getMessage();
                publishProgress(4);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if (values[0] == 1){
                spinnerAdapter = new SpinnerItemsArrayAdapter(Principal.this,spinnerItems);
                cboDocentes.setAdapter(spinnerAdapter);
                cboDocentes.setSelection(0);

                cboDocentes.post(new Runnable() {
                    @Override
                    public void run() {
                        timer.cancel();
                        timer.start();
                    }
                });


            }
        }
    }



    private class AsyncMensajes extends AsyncTask<Object,Integer, Integer>{

        @Override
        protected Integer doInBackground(Object... params) {
            publishProgress(0);
            MensajesItems.clear();
            ArrayList<Object>  parametros = new ArrayList<>(5);
            parametros.add(0, "idRepresentante*" + params[0]);
            parametros.add(1, "idDocente*" + params[1]);
            parametros.add(2, "estado*"+ params[2]);
            parametros.add(3, "limite*"+ params[3]);
            parametros.add(4, "getMensajesRep");

            respuesta ws = new respuesta();
            response = ws.getData(parametros);

            //Log.d("EJVH MSJ", response.getClass().toString());
            //Log.d("EJVH MSJ", response.toString());

            try
            {
                JSONObject jsonObj = new JSONObject(response.toString());

                String result = jsonObj.get("Result").toString();

                switch (result) {
                    case "OK":
                        JSONArray array = jsonObj.getJSONArray("Mensajes");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject mensaje = array.getJSONObject(i);
                            String value = "";

                            if (mensaje.get("fechaHora").toString().matches("^/Date\\(\\d+\\)/$")) {
                                value = mensaje.get("fechaHora").toString().replaceAll("^/Date\\((\\d+)\\)/$", "$1");
                            }

                            MensajesItems.add(new lvMensajesItems(
                                    mensaje.getInt("IdMensaje"),
                                    mensaje.getInt("Via"),
                                    mensaje.getInt("Estado"),
                                    Long.parseLong(value),
                                    mensaje.getString("Texto")
                            ));
                        }
                        publishProgress(1);
                        break;
                    case "NO ROWS":
                        mensaje = jsonObj.get("Message").toString();
                        publishProgress(2);
                        break;
                    default:
                        mensaje = jsonObj.get("Message").toString();
                        publishProgress(3);
                        break;
                }
                return null;

            }
            catch (JSONException e) {
                mensaje = e.getMessage();
                publishProgress(4);
                return null;
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 1){
                adapter = new lvMensajesItemsArrayAdapter(Principal.this,MensajesItems);
                lvMensajes.setAdapter(null);
                lvMensajes.setAdapter(adapter);
            }
            if (values[0] == 2){
                lvMensajes.setAdapter(null);
            }
        }
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

                switch (result) {
                    case "OK":
                        JSONArray array = jsonObj.getJSONArray("Calendario");
                        CalendarioItems = new lvCalendarioItems[array.length()];

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject evento = array.getJSONObject(i);
                            CalendarioItems[i] = new lvCalendarioItems(
                                    evento.getString("Header"),
                                    evento.getInt("Representado"),
                                    evento.getInt("Antiguedad"),
                                    evento.getString("Titulo"),
                                    evento.getString("Fecha"),
                                    evento.getString("Descripcion"));
                        }
                        publishProgress(1);
                        break;
                    case "NO ROWS":
                        mensaje = jsonObj.get("Message").toString();
                        publishProgress(2);
                        break;
                    default:
                        mensaje = jsonObj.get("Message").toString();
                        publishProgress(3);
                        break;
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
                    switch (value){
                        case 0:
                            mensaje = "No hay eventos para esta semana";
                            break;
                        case 1:
                            mensaje = "No hay eventos para este mes";
                            break;
                        case 2:
                            mensaje = "No hay eventos para este año";
                            break;
                    }
                    lblSinEventos.setText(mensaje);
                    sinEventos.setVisibility(View.VISIBLE);
                    lvCalendario.setAdapter(null);

                    if (dialogMessage != null){
                        dialogMessage.dismiss();
                        dialogMessage = null;
                    }
                    break;
                default:
                    switch (value){
                        case 0:
                            mensaje = "No hay eventos para esta semana";
                            break;
                        case 1:
                            mensaje = "No hay eventos para este mes";
                            break;
                        case 2:
                            mensaje = "No hay eventos para este año";
                            break;
                    }
                    lblSinEventos.setText(mensaje);
                    sinEventos.setVisibility(View.VISIBLE);
                    lvCalendario.setAdapter(null);
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
            String direccion = "http://154.42.65.212:9600/schooltool.asmx";
            String metodo = parametros.get(parametros.size() - 1).toString();
            String soapAction = namespace + metodo;

            SoapObject request = new SoapObject(namespace, metodo);
            String property[];
            PropertyInfo pi;

            for (int i = 0; i < parametros.size() - 1; i++){
                property = parametros.get(i).toString().split("\\*");
                pi = new PropertyInfo();
                pi.setName(property[0]);
                pi.setValue(property[1]);
                pi.setType(property[1].getClass());
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
            dialogMessage = new CustomProgress(Principal.this,enProgreso,icono,msj);
            dialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogMessage.setCanceledOnTouchOutside(true);
            dialogMessage.show();
        }else{
            if(dialogMessage != null){
                dialogMessage.dismiss();
                dialogMessage = null;
            }

            dialogMessage = new CustomProgress(Principal.this,enProgreso,icono,msj);
            dialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogMessage.setCanceledOnTouchOutside(true);
            dialogMessage.show();
            CountDownTimer timer = new CountDownTimer(5000,1000) {
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
