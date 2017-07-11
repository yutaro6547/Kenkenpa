package com.example.yutarosuzuki.kenkenpa;

/**
 * Created by YUTARO SUZUKI on 2016/09/02.
 * ゲームの制限時間を定めるクラス
 */

public class CalcTime {
    private final int GAME_TIME = 10;
    private long mNowTime;
    private long mStartTime;

    public CalcTime() {
        this.mNowTime = GAME_TIME;
    }

    // 現在の時間を返す
    public long getNowTime(){
        return mNowTime;
    }
    // カウントダウン開始
    public void startCountDown(){
        mStartTime = System.currentTimeMillis();
    }

    // 現在の時間を計算
    // カウント終了でtrueを返す
    public boolean calc(){
        long current = System.currentTimeMillis();
        long time_gone = (current - mStartTime) / 1000;
        if(time_gone >= 30){
            mNowTime = 0;
        }else {
            mNowTime = GAME_TIME - time_gone;
        }
        if(mNowTime == 0) {
            return true;
        }
        return false;
    }
}
