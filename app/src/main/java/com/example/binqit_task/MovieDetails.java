package com.example.binqit_task;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetails extends AppCompatActivity {
    final static String TAG = "JARVIS";

    String selectedTitle;
    TextView mtitle,mYear,mImdb,mType;
    ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mtitle = (TextView) findViewById(R.id.movieTitle);
        mYear = (TextView) findViewById(R.id.releasedYear);
        mImdb = (TextView) findViewById(R.id.imdbId);
        mType = (TextView) findViewById(R.id.type);
        mPoster = (ImageView) findViewById(R.id.moviePoster);
        Intent intent = getIntent();
        selectedTitle = intent.getStringExtra("Title");
        Log.d(TAG, selectedTitle);
        mtitle.setText(selectedTitle);
        getList();
        //Picasso.with(getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into(mPoster);

    }

    public void getList() {
        final String URL = "http://www.omdbapi.com/?apikey=5eb697cb&y=&s=movie&r=json";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }   // end failure

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something wih the result
                    Log.d(TAG, " GOT SOME DATA");
                    String jsonData = response.body().string();
                    // Log.d(TAG, jsonData);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonData);
                        JSONArray searchedArray = jsonObject.getJSONArray("Search");

                        if(searchedArray.length() > 0){
                            for (int i = 0; i < searchedArray.length(); i++){
                                JSONObject innerObject = searchedArray.getJSONObject(i);
                                Log.d(TAG, innerObject.toString());
                                Log.d("ARRAYNAME",innerObject.getString("Title"));
                                if(innerObject.getString("Title").equals(selectedTitle)){
                                    mYear.setText(innerObject.getString("Year"));
                                    mType.setText(innerObject.getString("Type"));
                                    mImdb.setText(innerObject.getString("imdbID"));
                                    Log.d("Poster", innerObject.getString("Poster"));


                                    String imagePath = innerObject.getString("Poster");
                                    final Bitmap bmp = doInBackground(imagePath);
                                    runOnUiThread(new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPoster.setImageBitmap(bmp);
                                        }
                                    }));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }   // end response
        });   // end new call
    }

    protected Bitmap doInBackground(String url) {
        String pathToFile = url;
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(pathToFile).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    public void backButtonPressed(View view){
        Intent id = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(id);
        setContentView(R.layout.activity_main);
    }
}