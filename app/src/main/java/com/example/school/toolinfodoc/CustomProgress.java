package com.example.school.toolinfodoc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomProgress extends Dialog{

    private Boolean inProgress;
    private int icono;
    private String mensaje;
    private Activity b;

    CustomProgress(Activity a, Context context, Boolean inProgress, int icono, String mensaje){
        super(a);

        this.inProgress = inProgress;
        this.icono = icono;
        this.mensaje = mensaje;
        this.b = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress);

        TextView lblMensajeProgress = (TextView) findViewById(R.id.lblMensajeProgress);
        ImageView iconoProgress = (ImageView) findViewById(R.id.imgProgress);
        View pbProgress = findViewById(R.id.pbProgress);

        if(inProgress){
            pbProgress.setVisibility(View.VISIBLE);
            iconoProgress.setVisibility(View.GONE);
            lblMensajeProgress.setText(mensaje);
        }else{
            pbProgress.setVisibility(View.GONE);

            switch (icono){
                case 0:
                    iconoProgress.setImageResource(R.drawable.iconomensaje_info);
                    break;
                case 1:
                    iconoProgress.setImageResource(R.drawable.iconomensaje_advertencia);
                    break;
                case 2:
                    iconoProgress.setImageResource(R.drawable.iconomensaje_error);
                    break;
                default:
                    break;
            }

            iconoProgress.setVisibility(View.VISIBLE);
            lblMensajeProgress.setText(mensaje);
        }
    }
}