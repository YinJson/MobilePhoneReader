package test.android.readerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private ProgressBar pb_welcome;

    private int index = 0;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

            if (msg.what == 0x111) {
                pb_welcome.setProgress(index);
                index+=5;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(0x111,100);
                if(index==100){
                    startActivity(new Intent(MainActivity.this,ChaActivity.class));
                    finish();
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb_welcome = (ProgressBar) findViewById(R.id.pb_welcome);

        mHandler.sendEmptyMessageDelayed(0x111,100);

    }
}
