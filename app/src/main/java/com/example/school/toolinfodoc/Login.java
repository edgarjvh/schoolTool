package com.example.school.toolinfodoc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;

import clases.Docente;
import clases.Representante;
import vistas.CustomProgress;

public class Login extends Activity {

    Object response = null;
    String mensaje = "";
    Representante representante;
    Docente docente;
    CustomProgress dialogMessage = null;
    RadioGroup rgroupTipoUsuario;
    String tipoUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText txtCedula = (EditText)findViewById(R.id.txtCedula);
        final EditText txtClave = (EditText)findViewById(R.id.txtClave);
        Button btnIngresar = (Button) findViewById(R.id.btnIngresar);
        rgroupTipoUsuario = (RadioGroup)findViewById(R.id.rgroupTipoUsuario);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton rbtn = (RadioButton)rgroupTipoUsuario.findViewById(rgroupTipoUsuario.getCheckedRadioButtonId());
                tipoUsuario = rbtn.getText().toString();

                if (txtCedula.getText().toString().trim().length() == 0){
                    mensaje = "Debe ingresar la cédula de identidad";
                    mostrarMensaje(false,false,1,mensaje);
                    return;
                }

                if (txtClave.getText().toString().trim().length() == 0){
                    mensaje = "Debe ingresar su contraseña";
                    mostrarMensaje(false,false,1,mensaje);
                    return;
                }


                new AsyncLogin().execute(txtCedula.getText().toString(),txtClave.getText().toString());
            }
        });
    }

    private class AsyncLogin extends AsyncTask<String, Integer, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            publishProgress(0);

            ArrayList<Object>  parametros = new ArrayList<>(4);
            parametros.add(0, "Cedula*" + params[0]);
            parametros.add(1, "Clave*"+ params[1]);
            parametros.add(2, tipoUsuario.equals("DOCENTE") ? "loginDocente" : "loginRepresentante");

            respuesta ws = new respuesta();
            response = ws.getData(parametros);

            Log.d("EJVH", response.getClass().toString());
            Log.d("EJVH", response.toString());

            try {
                JSONObject json = new JSONObject(response.toString());

                String result = json.get("Result").toString();

                if (result.equals("OK")){
                    String tipoClase = tipoUsuario.equals("DOCENTE") ? "Docente" : "Representante";
                    JSONObject array = new JSONObject(json.get(tipoClase).toString());

                    if (tipoUsuario.equals("DOCENTE")){
                        docente = new Docente();
                        docente.setId(array.getInt("Id"));
                        docente.setCedula(array.getInt("Cedula"));
                        docente.setNombres(array.get("Nombres").toString());
                        docente.setApellidos(array.get("Apellidos").toString());
                        docente.setTelefono1(array.getInt("Telefono1"));
                        docente.setTelefono2(array.getInt("Telefono2"));
                        docente.setDireccion(array.get("Direccion").toString());
                        publishProgress(1);
                        return 1;
                    }else{
                        representante = new Representante();
                        representante.setId(array.getInt("Id"));
                        representante.setCedula(array.getInt("Cedula"));
                        representante.setNombres(array.get("Nombres").toString());
                        representante.setApellidos(array.get("Apellidos").toString());
                        representante.setTelefono1(array.getInt("Telefono1"));
                        representante.setTelefono2(array.getInt("Telefono2"));
                        representante.setDireccion(array.get("Direccion").toString());
                        publishProgress(1);
                        return 1;
                    }
                }else{
                    mensaje = json.get("Message").toString();
                    publishProgress(2);
                    return 0;
                }
            } catch (JSONException e) {
                mensaje = e.getMessage();
                publishProgress(3);
                return 0;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            switch (values[0]){
                case 0:
                    mensaje = getResources().getString(R.string.iniciandoSesion);
                    mostrarMensaje(false,true,0,mensaje);
                    break;
                case 1:
                    if (tipoUsuario.equals("DOCENTE")){
                        mensaje = getResources().getString(R.string.bienvenidoCliente) + "\n" + docente.getNombres() + " " + docente.getApellidos();
                        mostrarMensaje(true, false, 0, mensaje);
                    }else{
                        mensaje = getResources().getString(R.string.bienvenidoCliente) + "\n" + representante.getNombres() + " " + representante.getApellidos();
                        mostrarMensaje(true, false, 0, mensaje);
                    }

                    break;
                case 2:
                    mostrarMensaje(false,false,2,mensaje);
                    break;
                case 3:
                    mostrarMensaje(false,false,2,mensaje);
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

    private void mostrarMensaje(Boolean esBienvenida, Boolean enProgreso,int icono, String msj){
        try{
            if(esBienvenida){
                if(dialogMessage != null) {
                    dialogMessage.dismiss();
                    dialogMessage = null;
                }

                dialogMessage = new CustomProgress(Login.this,enProgreso,icono,msj);
                dialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogMessage.setCanceledOnTouchOutside(false);
                dialogMessage.show();

                CountDownTimer timer = new CountDownTimer(3000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(Login.this, Principal.class);
                        i.putExtra("TipoUsuario",tipoUsuario);

                        if (tipoUsuario.equals("DOCENTE")){
                            i.putExtra("Docente",docente);
                        }else{
                            i.putExtra("Representante",representante);
                        }

                        startActivity(i);

                        if (dialogMessage != null){
                            dialogMessage.dismiss();
                            dialogMessage = null;
                        }
                    }
                };
                timer.start();
            }else{
                if(enProgreso){
                    if(dialogMessage != null){
                        dialogMessage.dismiss();
                        dialogMessage = null;
                    }

                    dialogMessage = new CustomProgress(Login.this,enProgreso,icono, msj);
                    dialogMessage.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogMessage.setCanceledOnTouchOutside(true);
                    dialogMessage.show();
                }else{
                    if(dialogMessage != null) {
                        dialogMessage.dismiss();
                        dialogMessage = null;
                    }

                    dialogMessage = new CustomProgress(Login.this,enProgreso,icono,msj);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


