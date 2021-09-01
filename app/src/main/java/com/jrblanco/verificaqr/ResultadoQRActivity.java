package com.jrblanco.verificaqr;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.jrblanco.verificaqr.databinding.ActivityResultadoqrActivityBinding;

public class ResultadoQRActivity extends AppCompatActivity {

    private ActivityResultadoqrActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityResultadoqrActivityBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());

        //--- Recuperamos los datos pasados para rellenar los textview
        Bundle extras = getIntent().getExtras();

        boolean estado = extras.getBoolean("ESTADO");
        this.binding.txtApellidos.setText(extras.getString("APELLIDOS"));
        this.binding.txtNombre.setText(extras.getString("NOMBRE"));
        this.binding.txtFechaNacimiento.setText(extras.getString("FECHANACIMIENTO"));
        this.binding.txtNombreCompleto.setText(extras.getString("NOMBRECOMPLETO"));

        this.binding.btnResultOK.setOnClickListener(v -> this.finish());

        //En función del estado de una forma o de otra
        if (estado) {
            this.resultadoVerificacionOK();
        } else {
            this.resuktadoVerificacionX();
        }

    }

    private void resultadoVerificacionOK(){
        this.binding.fondoConstraintLayout.setBackgroundColor(0xFF00C96B);
        this.binding.imagenResultado.setImageResource(R.drawable.fondocheck);
        this.binding.txtEstado.setText(R.string.resultadoOK);
    }

    private void resuktadoVerificacionX(){
        this.binding.fondoConstraintLayout.setBackgroundColor(0xFFE32626);
        this.binding.imagenResultado.setImageResource(R.drawable.fondonocheck);
        this.binding.txtEstado.setText(R.string.resultadoX);

    }
}