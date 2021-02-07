package com.borjalapa.hilosyhttp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //DAR PERMISOS DE INTERNET EN EL MANIFEST.XML

    private final static String URL_COMUNIDADES = "https://onthestage.es/restapi/v1/allcomunidades";
    private final static String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=Madrid,es&APPID=afe65bb24deaa16640c55f532603c7c6";
    private final static String TAG = "HILO";

    Button btnTarea, btnComunidades;
    TextView tvResultado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTarea = findViewById(R.id.btnTarea);

        tvResultado = findViewById(R.id.tvResultado);

        btnTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ejecutarTarea();
                //ejecutarTareaHilo();
                //ejecutarAsyncTask();
                obtenerTiempo();
            }
        });

        btnComunidades = findViewById(R.id.btnGetComunidades);

        btnComunidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComunidades();
            }
        });
    }

    private void obtenerTiempo() {
        new GetAsync(new GetAsync.IAsyncGet() {
            @Override
            public void onFinish(Tiempo t) {
                //Cuando termina de hacer lo que sea
                tvResultado.setText("Tiempo: " + t.kelvinToCelsius(t.temperatura) +"ºC\n" +"Descripción: " + t.descripcion + "\n" +"Humedad: " + t.humedad + "%");
            }
        }).execute(API_URL);
    }

    private void ejecutarAsyncTask() {
        MiTareaAsincrona miTareaAsincrona = new MiTareaAsincrona(10);
        miTareaAsincrona.execute(new Integer[]{1,2});
    }

    private void ejecutarTareaHilo() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "Hilo iniciado");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                Log.i(TAG, "Hilo finalizado");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResultado.setText("FIN HILO");
                    }
                });

            }
        };
        Thread hilo = new Thread(r);
        hilo.start();
    }

    //tarda 5 segundos en cambiar el texto
    private void ejecutarTarea() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tvResultado.setText("Finalizada Tarea");
    }

    class MiTareaAsincrona extends AsyncTask<Integer, String, String>{

        Integer fin;
        MiTareaAsincrona(Integer fin){
            this.fin = fin;
        }

        @Override
        protected String doInBackground(Integer... numeros) {

            Log.i(TAG,"Inicio background" + numeros[1]);

            for (int i=numeros[0]; i<=fin; i++){
                //envia al progressUpdate lo que tiene que mostrar
                publishProgress(""+i);
                try {
                    //lo hace cada segundo
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.i(TAG,"Fin background");

            //envia al onPostExecute lo que mostrar al acabar
            return "fin";
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG,"onPreExecute");
            super.onPreExecute();
            tvResultado.setText("0");
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i(TAG,"onPostExecute");
            super.onPostExecute(s);
            tvResultado.setText(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.i(TAG,"onProgressUpdate");
            super.onProgressUpdate(values);
            tvResultado.setText(values[0]);
        }
    }

    private void getComunidades(){
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_COMUNIDADES, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String s ="";
                try{

                    JSONArray datos = response.getJSONArray("DATA");
                    for(int i = 0;i<datos.length();i++){
                        JSONObject comunidad = datos.getJSONObject(i);
                        s +="\n" + comunidad.getString("descripcion");
                    }
                }catch (JSONException e){

                }

                tvResultado.setText(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

}