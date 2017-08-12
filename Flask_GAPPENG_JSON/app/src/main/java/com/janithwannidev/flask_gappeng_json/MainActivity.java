package com.janithwannidev.flask_gappeng_json;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public TextView textView;
    public EditText editText;
    public String input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.txt);
        editText = (EditText) findViewById(R. id.editText);
        input = editText.getText().toString();
        Log.i(this.getClass().getCanonicalName(),input+"is the input");
    }

    public void postdata(View v){
        new Thread(){
            @Override
            public void run(){
                HandleHTTP h = new HandleHTTP(input);
                h.execute();

            }
        }.start();
    }

    private class HandleHTTP extends AsyncTask<Void,Void,Void>{

        int result = 0;
        String response;
        String txtinput;
        public HandleHTTP(String value){
            txtinput = value;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.i(this.getClass().getCanonicalName(),"Starting up and connecting");
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HashMap<String,String> postParams = new HashMap<>();
                postParams.put("value",txtinput);
                Log.i(this.getClass().getCanonicalName(),"txinput is "+txtinput);
                URL url = new URL("https://cloud-sql-176109.appspot.com/data");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                writer.write(getPostDataString(postParams));
                writer.flush();writer.close();
                outputStream.close();
                int responseCode = connection.getResponseCode();
                Log.i(this.getClass().getCanonicalName(),responseCode+"this is the response");
                if(responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    while((line=br.readLine()) != null){
                        response += line;
                    }
                }else{
                    response = "ERROR";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for(Map.Entry<String, String> entry : params.entrySet()){
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            textView.setText(response);
        }
    }
}
