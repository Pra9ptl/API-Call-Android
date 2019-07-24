package com.example.binqit_task;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // used for Log.d statements
    // -----------
    final static String TAG = "JARVIS";

    // 1. OKHttp variable
    OkHttpClient client = new OkHttpClient();
    String[] titleList;
    ListView tList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleList = new String[10];
        getList();
        bindList();
        tList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent id = new Intent(getApplicationContext(),MovieDetails.class);
                id.putExtra("Title",titleList[i]);
                startActivity(id);
            }
        });
    }

    public void bindList(){
        tList = (ListView) findViewById(R.id.listTitle);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titleList);
        tList.setAdapter(adapter);
    }

    public void getList() {
        String URL = "http://www.omdbapi.com/?apikey=5eb697cb&y=&s=movie&r=json";
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
                    Log.d(TAG, jsonData);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonData);
                        JSONArray searchedArray = jsonObject.getJSONArray("Search");

                        if(searchedArray.length() > 0){
                            for (int i = 0; i < searchedArray.length(); i++){
                                JSONObject innerObject = searchedArray.getJSONObject(i);
                                String title = innerObject.getString("Title");
                                titleList[i] = title;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }   // end response
        });   // end new call


    }

}