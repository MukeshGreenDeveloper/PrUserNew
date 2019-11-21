package bz.pei.driver.ui.drawerscreen;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import bz.pei.driver.R;

public class EmptyActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_job);
/*
        LinearTimerView linearTimerView = (LinearTimerView)
                findViewById(R.id.time_layout_accept_reject);

        final LinearTimer linearTimer = new LinearTimer.Builder()
                .linearTimerView(linearTimerView)
                .duration(10 * 1000)
                .build();
        findViewById(R.id.btn_accept_acceptreject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearTimer.startTimer();
            }
        });
        findViewById(R.id.btn_reject_acceptreject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearTimer.restartTimer();
            }
        });
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
