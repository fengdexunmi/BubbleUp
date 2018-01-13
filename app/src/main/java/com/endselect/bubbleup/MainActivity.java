package com.endselect.bubbleup;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.endselect.bubbleup.widget.CircleBadge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleBadge circleBadge = findViewById(R.id.circle_badge);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                circleBadge.setCompletionRatioAnimated(0.8f);
            }
        }, 600);
    }
}
