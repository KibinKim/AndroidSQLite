package com.example.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private MyThread mThread;
    private int mNumber;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = (TextView)findViewById(R.id.textResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mThread = new MyThread();
        mThread.start(); //스레드 시작
    }

    @Override
    protected void onStop() {
        super.onStop();
        Message msg = Message.obtain();
        msg.what = -1;
        mThread.mHandler.sendMessage(msg); //메시지 보내기; 루퍼 & 스레드 종
    }

    public void mOnClick(View v) {
        Message msg = Message.obtain();
        msg.what = 0;
        msg.arg1 = ++mNumber;
        mThread.mHandler.sendMessage(msg); //메시지 보내기; 처리할 숫자 데이터
    }

    @SuppressLint("HandlerLeak") //Handler warning 처리
    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                textResult.setText(msg.arg1 + "*" + msg.arg1 + "=" + msg.arg2);
            }
        }
    };

    private class MyThread extends Thread {
        public Handler mHandler;
        @SuppressLint("HandlerLeak") //Handler warning처리
        @Override
        public void run() {
            Looper.prepare(); //루퍼 초기화
            mHandler = new Handler() { //핸들러 생성
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == 0) {
                        //계산 지연 시간
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //계산 결과 전송
                        int result = msg.arg1 * msg.arg1;
                        Message msgResult = Message.obtain();
                        msgResult.what = 0;
                        msgResult.arg1 = msg.arg1;
                        msgResult.arg2 = result;
                        mMainHandler.sendMessage(msgResult);
                    } else if (msg.what == -1) {
                        Looper.myLooper().quit(); //루퍼종료 -> 스레드 종료
                        Log.d("test", "루퍼를 종료");
                    }
                }
            };

            Looper.loop(); //루퍼 실행
            Log.d("test", "스레드 종료");
        }
    }
}
