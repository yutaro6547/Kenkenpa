package com.example.yutarosuzuki.kenkenpa;




import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by you on 2016/09/01.
 */
public class TileQuestion {

    private final float mTop;
    private final float mBottom;
    private final float mRight;
    private final float mLeft;
    private final String mAl;
    private final String mText = "を踏め!";

    public TileQuestion(float top, float left, float bottom, float right, String al) {
        mTop = top;
        mLeft = left;
        mBottom = bottom;
        mRight = right;
        mAl = al;
    }

    public void draw(Canvas canvas, Paint paint){
        // 文字の座標
        float x =  (mRight + mLeft) / 2;
        float y = (mTop + mBottom) / 2;
        // アルファベットを描画
        paint.setTextSize(200);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        // アルファベットが画面上部に来るように調整
        canvas.drawText(mAl, x - 70, y + 70, paint);
        //テキストを描画
        paint.setTextSize(150);
        paint.setColor(Color.BLACK);
        //テキストがアルファベットの隣に来るように調整
        canvas.drawText(mText, x + 100, y + 70, paint);
    }
}
