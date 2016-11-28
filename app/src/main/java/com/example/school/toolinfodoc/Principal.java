package com.example.school.toolinfodoc;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TabHost;
import android.widget.TextView;

public class Principal extends AppCompatActivity {

    private Representante representante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

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

        tabs.setCurrentTab(1);
    }
}
