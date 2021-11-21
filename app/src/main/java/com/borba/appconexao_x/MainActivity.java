package com.borba.appconexao_x;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String REQUEST_URL = "https://webfonts.googleapis.com/v1/webfonts?sort=POPULARITY&key=AIzaSyCP1BkjWY2eTaNWXTZ0H9xVNXdOmlno1FM";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FontAsyncTask task = new FontAsyncTask();
        task.execute();
    }

    private void updadeUi(Event fontData) {

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(fontData.familia);

        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(fontData.ultimaMod);

        TextView tsunamiTextView = (TextView) findViewById(R.id.cat);
        tsunamiTextView.setText(fontData.categoria);

    }

    private class FontAsyncTask extends AsyncTask<URL, Void, Event> {
        @Override
        protected Event doInBackground(URL... urls) {
            URL url = null;
            try {
                url = new URL(REQUEST_URL);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Erro ao criar URL", e);
                return null;
            }
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {

            }
            Event fontData = extractFeatureFromJson(jsonResponse);

            return fontData;
        }

        @Override
        protected void onPostExecute(Event fontData) {
            if (fontData == null) {
                return;
            }

            updadeUi(fontData);
        }


        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private Event extractFeatureFromJson(String fontDataJSON) {
            if (TextUtils.isEmpty(fontDataJSON)) {
                return null;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(fontDataJSON);
                JSONArray itemArray = baseJsonResponse.getJSONArray("items");

                if (itemArray.length() > 0) {
                    JSONObject firstItem = itemArray.getJSONObject(00);//Alterar indice altera tipo de fonte

                    String family = firstItem.getString("family");
                    String lastModified = firstItem.getString("lastModified");
                    String category = firstItem.getString("category");

                    return new Event(family, lastModified, category);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problema no parsing do JSON fontData", e);
            }
            return null;
        }
    }
}