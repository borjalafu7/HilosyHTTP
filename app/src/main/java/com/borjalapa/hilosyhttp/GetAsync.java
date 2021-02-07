package com.borjalapa.hilosyhttp;

import android.os.AsyncTask;

import com.borjalapa.hilosyhttp.Utilidades.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetAsync extends AsyncTask<String,String, String> {

    IAsyncGet iAsyncGet;
    String url;

    @Override
    protected String doInBackground(String... urls) {
        return Utilidades.ObtenerDatos(urls[0]);
    }

    public GetAsync(IAsyncGet iAsyncGet) {
        super();
        this.iAsyncGet = iAsyncGet;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject root = new JSONObject(s);

            JSONArray tiempos = root.getJSONArray("weather");
            JSONObject tiempo = tiempos.getJSONObject(0);

            JSONObject principal = root.getJSONObject("main");


            Double temperatura = principal.getDouble("temp");
            String descripcion = tiempo.getString("description");
            Integer humedad = principal.getInt("humidity");

            Tiempo t = new Tiempo(temperatura,descripcion,humedad);
            iAsyncGet.onFinish(t);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    public interface IAsyncGet{
        public void onFinish(Tiempo t);
    }
}