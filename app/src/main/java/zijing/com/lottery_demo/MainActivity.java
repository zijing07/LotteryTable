package zijing.com.lottery_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private LuckyPanView luckyPanView;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button) findViewById(R.id.click_button);
        luckyPanView = (LuckyPanView) findViewById(R.id.lucky_pan_view);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckyPanView.isStart()) {
                    luckyPanView.luckyStart(1);
                    startBtn.setText("停止");
                } else {
                    startBtn.setText("开始");
                    luckyPanView.luckyEnd();
                }
            }
        });
    }
}
