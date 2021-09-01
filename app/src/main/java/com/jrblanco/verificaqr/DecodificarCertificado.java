package com.jrblanco.verificaqr;

import com.google.iot.cbor.CborMap;
import com.google.iot.cbor.CborParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import COSE.CoseException;
import COSE.Encrypt0Message;
import COSE.Message;
import nl.minvws.encoding.Base45;

public class DecodificarCertificado
{
    private static final int BUFFER_SIZE = 1024;
    boolean error;
    private DatosCertificado datosCertificado;
    private String codigoQR;

    public DecodificarCertificado(String codigoQR) {
        this.error = false;
        this.codigoQR = codigoQR;
    }

    public DatosCertificado decodificarQR(){

        //Decodifica el QR y obtenemos el QR Comprimido con zLIB
        byte[] qrComprimido = this.decodificaBase45(this.codigoQR);

        //Descomprime con Zlib
        ByteArrayOutputStream qrdescomprimido = descomprimeQR(qrComprimido);

        //Decodifica el mensaje COSE
        Message mensajeCBOR = mensajeCOSE(qrdescomprimido);

        //Obtiene la estructura JSON
        String estructuraJSON = mapaCbor(mensajeCBOR);

        //Estrae los datos que necesitamos del JSON
        datosCertificado = this.leerQrJson(estructuraJSON);

        if (this.datosCertificado != null) {
            return this.datosCertificado;
        }
        return null;
    }
    private DatosCertificado leerQrJson(String estructuraJSON){
        DatosCertificado datos = new DatosCertificado();

        if (!this.error){
            //Se va rellenado el objeto datosdetificado con los datos del JSON
            JSONObject object = null;
            try {

                object = new JSONObject(estructuraJSON);
                datos.setApellidos(object.getJSONObject("-260").getJSONObject("1").getJSONObject("nam").getString("fn"));
                datos.setNombre(object.getJSONObject("-260").getJSONObject("1").getJSONObject("nam").getString("gn"));
                datos.setNombrecompleto(object.getJSONObject("-260").getJSONObject("1").getJSONObject("nam").getString("fnt") +
                        "<<" +
                        object.getJSONObject("-260").getJSONObject("1").getJSONObject("nam").getString("gnt"));

                datos.setFechaNacimiento(object.getJSONObject("-260").getJSONObject("1").getString("dob"));

                //----- Acceso a los datos de la vacunación
                JSONArray seccion = object.getJSONObject("-260").getJSONObject("1").names();

                //v = Vacuna t = PCR negativa r = Pasado el covid

                if (seccion.get(0).equals("v")) { //"v" si el certificado es de vacunación entra
                    JSONArray vacuna =  object.getJSONObject("-260").getJSONObject("1").getJSONArray("v");
                    JSONObject v = (JSONObject) vacuna.get(0);

                    if (v.getString("dn").equals(v.getString("sd"))) { //Si las pautas coinciden esta correcta la vacunación
                        datos.setEstado(true);
                    } else {
                        datos.setEstado(false);
                    }
                } else if (seccion.get(0).equals("t")) { //Si el certificado es de test de PCR está correcto
                    datos.setEstado(true);
                } else if (seccion.get(0).equals("r")) { //El certificado es de Recuperación es decir que ha pasado la enfermedad
                    JSONArray recuperacion =  object.getJSONObject("-260").getJSONObject("1").getJSONArray("r");
                    JSONObject r = (JSONObject) recuperacion.get(0);
                    String fechaMax = r.getString("du");
                    fechaMax = fechaMax.replace("-","/") + " 00:00";
                    /*
                     * Convertimos la fecha en String en DATE
                     */
                    Date dateFechaMax = null;
                    try {
                        dateFechaMax = new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(fechaMax);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date fechaActual = new Date(); //Contiene la fecha del sistema actual
                    if (fechaActual.before(dateFechaMax)) { //La fecha del sistema tiene que ser antes de la del certificado
                        datos.setEstado(true);
                    } else {
                        datos.setEstado(false);
                    }
                }
                return datos;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String mapaCbor(Message mnjCbor){
        CborMap cborMap = null;
        if (!this.error){
            try {
                cborMap = CborMap.createFromCborByteArray(mnjCbor.GetContent());
                return cborMap.toJsonString(); //JSON del Certificado
            } catch (CborParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private Message mensajeCOSE(ByteArrayOutputStream qrDes){
        Message a = null;
        if (!this.error){
            try {
                a = Encrypt0Message.DecodeFromBytes(qrDes.toByteArray());
                return a;
            } catch (CoseException e) {
                e.printStackTrace();
            }
        }
        return a;
    }

    private ByteArrayOutputStream descomprimeQR(byte[] bytecomprimido){
        //3.- Inflate es decir descomprimimos con Zlib
        // https://code.i-harness.com/es/docs/openjdk~8/java/util/zip/inflater
        Inflater inflater = new Inflater();
        inflater.setInput(bytecomprimido);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytecomprimido.length);
        byte[] buffer = new byte[BUFFER_SIZE];
        while (!inflater.finished()){
            try {
                final int count = inflater.inflate(buffer);
                outputStream.write(buffer,0,count);
            } catch (DataFormatException e) {
                e.printStackTrace();
                this.error = true;
                break;
            }
        }
        if (!this.error) {
            return outputStream;
        }
        return null;
    }

    private byte[] decodificaBase45(String datoQR){
        //1.- Quitamos los primeros caracteres "HC1:"
        String textQR = datoQR.substring(4);

        //2.- decodificamos el textQR con el decode base45
        return Base45.getDecoder().decode(textQR);
    }
}
