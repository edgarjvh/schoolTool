package com.example.school.toolinfodoc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;



public class MainActivity extends Activity {

    Object response = null;
    String mensaje = "";
    Representante representante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText txtCedula = (EditText)findViewById(R.id.txtCedula);
        final EditText txtClave = (EditText)findViewById(R.id.txtClave);
        Button btnIngresar = (Button) findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtCedula.getText().toString().trim().length() == 0){
                    Log.d("EJVH", "Debe ingresar la cedula");
                    return;
                }

                if (txtClave.getText().toString().trim().length() == 0){
                    Log.d("EJVH", "Debe ingresar la contraseña");
                    return;
                }

                new AsyncLogin().execute(txtCedula.getText().toString(),txtClave.getText().toString());
            }
        });
    }

    private class AsyncLogin extends AsyncTask<String, Integer, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            ArrayList<Object>  parametros = new ArrayList<>(4);
            parametros.add(0, "Cedula*" + params[0]);
            parametros.add(1, "Clave*"+ params[1]);
            parametros.add(2, "loginRepresentante");

            respuesta ws = new respuesta();
            response = ws.getData(parametros);

            Log.d("EJVH", response.getClass().toString());
            Log.d("EJVH", response.toString());

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                if (result.equals("OK")){
                    JSONObject array = new JSONObject(json.get("Representante").toString());
                    representante = new Representante();
                    representante.setId(array.getInt("Id"));
                    representante.setCedula(array.getInt("Cedula"));
                    representante.setNombres(array.get("Nombres").toString());
                    representante.setApellidos(array.get("Apellidos").toString());
                    representante.setTelefono1(array.getInt("Telefono1"));
                    representante.setTelefono2(array.getInt("Telefono2"));
                    representante.setDireccion(array.get("Direccion").toString());
                    publishProgress(1);
                }else{
                    mensaje = json.get("Message").toString();
                    publishProgress(0);
                }
                return null;
            } catch (JSONException e) {
                mensaje = e.getMessage();
                publishProgress(0);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            switch (values[0]){
                case 0:
                    Log.d("EJVH Error", mensaje);
                    break;
                case 1:
                    Intent i = new Intent(MainActivity.this, Principal.class);
                    i.putExtra("Representante",representante);
                    startActivity(i);
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
                pi.setType(String.class);
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
}


