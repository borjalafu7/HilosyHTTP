package com.borjalapa.hilosyhttp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;


public class PostAsync extends AsyncTask<String, Integer, JSONObject> {
    private static final String TAG = "ERROR_POST";
    JSONObject postData, postResult = null;
    private TaskDelegateService taskDelegateService;


    public PostAsync() {

    }

    public PostAsync(TaskDelegateService taskDelegateService, Map<String, String> postData) {
        this.taskDelegateService = taskDelegateService;
        if (postData != null) {
            this.postData = new JSONObject(postData);
        }
    }

    public JSONObject getResultado() {
        return postData;
    }

    public void setResultado(JSONObject resultado) {
        this.postData = resultado;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;

        try {

            String sUrl = params[0];
            // This is getting the url from the string we passed in
            URL url = new URL(sUrl);

            // Create the urlConnection
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().toString());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setReadTimeout( 10000 /*milliseconds*/ );
            urlConnection.setConnectTimeout( 15000 /* milliseconds */ );

            urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.connect();

            // Send the post body
            if (this.postData != null) {
                DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                os.writeBytes(postData.toString());
                //clean up
                os.flush();
                os.close();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {
                Log.d("POST","Entra");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                in.close();

                // From here you can convert the string to JSON with whatever JSON parser you like to use
                // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method
            } else {
                postResult = null;
            }

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

        try {
            Log.d("POST",result.toString());
            postResult = new JSONObject(result.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return postResult;

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progreso = values[0].intValue();


    }

    @Override
    protected void onPreExecute() {


    }

    @Override
    protected void onPostExecute(JSONObject result) {
        taskDelegateService.taskServicePost(result);
    }

    @Override
    protected void onCancelled() {

    }

    public interface TaskDelegateService {
        public void taskServiceCompleted(JSONObject result);
        public boolean taskServicePost(JSONObject data);
        public void taskGetImage(Bitmap bm);
    }

}
