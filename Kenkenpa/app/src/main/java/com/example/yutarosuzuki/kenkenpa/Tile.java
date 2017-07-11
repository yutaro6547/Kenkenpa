package com.example.yutarosuzuki.kenkenpa;




import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;


/**
 * Created by YUTARO SUZUKI on 2016/08/31.
 * ゲーム画面におけるタイルの作成クラス
 *
 */
public class Tile {

    //一枚のタイルの位置、サイズ、文字を作成
    private final float mTop;
    private final float mBottom;
    private final float mRight;
    private final float mLeft;
    private final String mAl;

    public Tile(float top, float left, float bottom, float right, String al) {
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mAl = al;
    }

    //文字を取り出すためのgetter
    public String getAl(){
        return mAl;
    }

    public float getTop(){
        return mTop;
    }

    //タイル左辺を取り出すためのgetter
    public float getLeft(){
        return mLeft;
    }

    //タイルの底辺を取り出すためのgetter
    public float getBottom(){
        return mBottom;
    }

    //タイルの右辺を取り出すためのgetter
    public float getRight(){
        return mRight;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void draw(Canvas canvas, Paint paint){
        // 文字の座標
        float x =  (mRight + mLeft) / 2;
        float y = (mTop + mBottom) / 2;

        // 塗りつぶし部分を描画
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mLeft, mTop, mRight, mBottom, 700, 700,paint);

        // 文字を描画
        paint.setTextSize(200);
        paint.setColor(Color.GRAY);
        // 文字がタイルの真ん中に来るように調整
        canvas.drawText(mAl, x-70, y + 70, paint);

        // 枠線部分を描画
        paint.setARGB(173,216,230,255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        canvas.drawRoundRect(mLeft, mTop, mRight, mBottom, 700, 700, paint);

    }

}
