package com.zijing.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LuckyPanView luckyPanView;
    private Button startBtn;
    private TextView resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startBtn = (Button) findViewById(R.id.click_button);
        luckyPanView = (LuckyPanView) findViewById(R.id.lucky_pan_view);
        resultTv = (TextView) findViewById(R.id.result_index);

        luckyPanView.setOnSpinFinshedCallback(new LuckyPanView.OnSpinFinshedCallback() {
            @Override
            public void onSpoinFinished(final int resultIndex) {
                resultTv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resultTv.setText("被选中的结果是: " + resultIndex);
                    }
                }, 200);
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckyPanView.isStart()) {
                    luckyPanView.luckyStart();
                    startBtn.setText("停止");
                } else {
                    startBtn.setText("开始");
                    luckyPanView.luckyEnd();
                }
            }
        });
    }
}
