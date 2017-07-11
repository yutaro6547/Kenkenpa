package com.example.yutarosuzuki.kenkenpa;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;


/**
 * Created by YUTARO SUZUKI on 2016/08/24.
 * ゲームを描画するためのTexture View
 */
public class GameView extends TextureView implements TextureView.SurfaceTextureListener, View.OnTouchListener {
    private Thread mThread;
    // Activityの状態を独自のThreadへ状態を渡すための変数mIsRunnable
    // volatileは更新していない変数の値を使うために指定する
    volatile private boolean mIsRunnable;
    // x座標のタッチした位置を取得するためのメンバ変数mTouchedX
    // volatile private float mTouchedX;
    private Tile[][] mTile;
    private TileQuestion mTileQuestion;
    private String mIndex;
    private static Random rnd = new Random();
    private int mCount = 0;
    private long GAME_TIME = 2000;
    private long mGameStartTime;
    private long mNowTime;
    private Handler mHandler;
    private int mTimeCount;

    /*
     *スーパークラスにはデフォルトコンストラクターがないので引数付きのコンストラクターを明示的に呼び出す
     * @param context Activity
     */
    public GameView(final Context context){
        super(context); //親クラスのコンストラクターを引数contextで呼び出す
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                Intent intent = new Intent(context,ClearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(message.getData());
                context.startActivity(intent);
            }
        };
        onFinishInflate();
    }

    public void start() {
        // ゲームを描画するための独自Threadを作る
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 画面を描画するのに使うのがPaint
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                while (true) {
                    mNowTime = System.currentTimeMillis();
                    // アプリの実行中繰り返し呼ばれる
                    synchronized (GameView.this) {
                        if(!mIsRunnable){
                            break; //ループを終了する
                        }
                        // lockCanvas()でCanvasを取得しロックをかけ、画面を描画する処理を行う
                        // 排他制御…特定のリソースを１つの処理が専有すること
                        Canvas canvas = lockCanvas();
                        if (canvas == null) {
                            continue;
                        }
                        //リフレッシュするためのもの
                        canvas.drawColor(Color.rgb(173,216,230));
                        //行列のタイルを描画する
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                mTile[i][j].draw(canvas, paint);
                            }
                        }
                        //最上部の出題をする部分の描画
                        mTileQuestion.draw(canvas, paint);
                        if((mNowTime - mGameStartTime) >= 1000){
                            mTimeCount++;
                            mNowTime = mGameStartTime;
                        }
                        if((GAME_TIME - mTimeCount) <= 0){
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR,true);
                            bundle.putInt(ClearActivity.EXTRA_TILE_COUNT,mCount);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }
                        // unlockCanvasAndPost()でCanvasのロックを解除し、画面に反映する
                        unlockCanvasAndPost(canvas);
                        long sleepTime = 16 - System.currentTimeMillis() + mNowTime;
                        if(sleepTime > 0){
                            try{
                                Thread.sleep(sleepTime);
                            }catch(InterruptedException e){
                            }
                        }
                    }
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }

    public void stop() {
        mIsRunnable = false;
    }

    // オブジェクトを作るためのメソッド
    // 画面サイズを取得するための処理の一つ
    // onSurfaceTextureAvailable()やonSurfaceTextureSizeChanged()での処理をまとめるためのもの

    public void readyObjects(int width, int height){
        float tileWidth = width / 4;
        float tileHeight = height / 5;
        //最初の２次元配列のタイルを生成する
        mTile = new Tile[4][4];
        mGameStartTime = System.currentTimeMillis();
        for(int  i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                float tileTop = j * tileHeight + tileHeight;
                float tileLeft = i * tileWidth;
                float tileBottom = tileTop + tileHeight;
                float tileRight = tileLeft + tileWidth;
                String al = RandomStringUtils.random(1,"ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                mTile[i][j] = new Tile(tileTop, tileLeft, tileBottom, tileRight, al);
            }
        }
        // mTile[0][3], mTile[1][3], mTile[2][3], mTile[3][3]
        // indexは列方向の配列の添え字を表す
        //最下辺のタイルの中の文字の中からランダムに出題のタイルを結び付けるもの
        int index = rnd.nextInt(4);
        mIndex =  mTile[index][3].getAl();
        mTileQuestion = new TileQuestion(150,0,150,200,mIndex);

    }

    // 出題タイルとタッチしたタイルの正誤判定メソッド
    public boolean tileJudge(float touchedX){
        float judgeLeft = mTile[0][3].getLeft();
        float judgeMiddleLeft = mTile[1][3].getLeft();
        float judgeMiddleRight = mTile[2][3].getLeft();
        float judgeRight = mTile[3][3].getLeft();
        if(judgeLeft <= touchedX && touchedX < judgeMiddleLeft){
            if(mIndex.equals(mTile[0][3].getAl())) {
                return true;
            }
        }else if(judgeMiddleLeft <= touchedX && touchedX < judgeMiddleRight){
            if(mIndex.equals(mTile[1][3].getAl())) {
                return true;
            }
        }else if(judgeMiddleRight <= touchedX && touchedX < judgeRight){
            if(mIndex.equals(mTile[2][3].getAl())) {
                return true;
            }
        }else if(judgeRight <= touchedX) {
            if (mIndex.equals(mTile[3][3].getAl())) {
                return true;
            }
        }
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR,false);
            bundle.putInt(ClearActivity.EXTRA_TILE_COUNT,mCount);
            message.setData(bundle);
            mHandler.sendMessage(message);
        return false;
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //Canvasの画面を描画する最中に画面を破棄しないように同期した
        synchronized (this) {
            return true;
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    // タッチイベントを取得する
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // タッチ後に行の処理を行う
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN
                && tileJudge(event.getX())){
            mCount++;
            for(int i = 0; i < 4; i++) {
                // 最上段の行のタイルのサイズを決める
                float tileTop = mTile[i][0].getTop();
                float tileLeft = mTile[i][0].getLeft();
                float tileBottom = mTile[i][0].getBottom();
                float tileRight = mTile[i][0].getRight();
                for(int j = 3; j > 0; j--) {
                    mTile[i][j] = new Tile(mTile[i][j].getTop(), mTile[i][j].getLeft()
                            , mTile[i][j].getBottom(), mTile[i][j].getRight(), mTile[i][j-1].getAl());
                }
                //ABCDEFGHIJKLMNOPQRSTUVWXYZ
                String al = RandomStringUtils.random(1,"ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                //String al="ABCD".substring(i,i+1);
                mTile[i][0]=new Tile(tileTop, tileLeft, tileBottom, tileRight, al);
            }
            // mTile[0][3], mTile[1][3], mTile[2][3], mTile[3][3]
            // indexは列方向の配列の添え字を表す
            //最下辺のタイルの中の文字の中からランダムに出題のタイルを結び付けるもの
            int index = rnd.nextInt(4);
            mIndex =  mTile[index][3].getAl();
            mTileQuestion = new TileQuestion(150,0,150,200,mIndex);
        }
        return true;
    }
}
