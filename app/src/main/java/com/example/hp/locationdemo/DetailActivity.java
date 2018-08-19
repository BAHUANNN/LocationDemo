package com.example.hp.locationdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.MapView;

public class DetailActivity extends AppCompatActivity {

    private static final String DETAIL_TRANS_CODE = "detail";

    public static void start(Context context, String s){
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(DETAIL_TRANS_CODE,s);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initLocation();
    }

    private void initLocation() {
    }

    private void initView() {
    }

}
