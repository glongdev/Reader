package com.glong.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.glong.sample.activities.ExtendReaderActivity;
import com.glong.sample.activities.NormalReaderActivity;
import com.glong.sample.activities.SimpleReaderActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.simple_reader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SimpleReaderActivity.class));
            }
        });

        findViewById(R.id.normal_reader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NormalReaderActivity.class));
            }
        });

        findViewById(R.id.extend_reader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExtendReaderActivity.class));
            }
        });
    }
}
