package com.jrblanco.verificaqr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jrblanco.verificaqr.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imagenInfo.setOnClickListener(v -> {
            /*
                Abre el Activity de la info   InfoActivity.class
             */
            Intent intentInfo = new Intent(this,InfoActivity.class);
            startActivity(intentInfo);
        });

        binding.btnIniciar.setOnClickListener(v -> {
            /*
                Inicia la camara para escanear QR
            */
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Escanear QR del certificado de vacunación");
            integrator.setCameraId(0);  // Usar la camara principal
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this,"Escaneo del certificado ha sido cancelado", Toast.LENGTH_SHORT).show();
            } else {
                if (result.getContents().length()>500) {
                    DecodificarCertificado decodificarCertificado = new DecodificarCertificado(result.getContents());

                    DatosCertificado datos = decodificarCertificado.decodificarQR();

                    if (datos != null) {
                        Intent intentResultado = new Intent(this, ResultadoQRActivity.class);
                        intentResultado.putExtra("ESTADO", datos.getEstado());
                        intentResultado.putExtra("APELLIDOS", datos.getApellidos());
                        intentResultado.putExtra("NOMBRE", datos.getNombre());
                        intentResultado.putExtra("FECHANACIMIENTO", datos.getFechaNacimiento());
                        intentResultado.putExtra("NOMBRECOMPLETO", datos.getNombrecompleto());
                        startActivity(intentResultado);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("NO SE HA PODIDO VERIFICAR EL CERTIFICADO COVID DE VACUNACIÓN")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                        AlertDialog mensaje = builder.create();
                        mensaje.setTitle("Error");
                        mensaje.show();
                    }
                } else {
                    Toast.makeText(this, "QR NO VALIDO", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}