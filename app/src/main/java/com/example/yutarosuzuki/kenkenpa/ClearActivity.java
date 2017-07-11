package com.example.yutarosuzuki.kenkenpa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClearActivity extends AppCompatActivity {
    public static final String EXTRA_IS_CLEAR = "com.example.yutarosuzuki.kenkenpa.EXTRA_IS_CLEAR";
    public static final String EXTRA_TILE_COUNT = "com.example.yutarosuzuki.kenkenpa.EXTRA_TILE_COUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        //他のActivityから値を受け取るときはIntentを使う
        Intent receiveIntent = getIntent();
        if(receiveIntent == null){
            finish();
        }
        //Intentの付加情報はExtras付与される
        Bundle receiveExtras = receiveIntent.getExtras();
        if(receiveExtras == null){
            finish();
        }
        boolean isClear = receiveExtras.getBoolean(EXTRA_IS_CLEAR,false);
        int tileCount = receiveExtras.getInt(EXTRA_TILE_COUNT,0);
        TextView textTitle = (TextView)findViewById(R.id.textTitle);
        TextView textTileCount = (TextView)findViewById(R.id.textTileCount);
        TextView textHighScore = (TextView)findViewById(R.id.textHighScore);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long highScore = sharedPreferences.getLong("high_score",0);


        if(highScore < tileCount && isClear){
            highScore = tileCount;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("high_score",highScore);
            editor.commit();
        }
        textHighScore.setText(getString(R.string.high_score,highScore));

        Button gameStart = (Button)findViewById(R.id.buttonGameStart);

        if(isClear){
            textTitle.setText(R.string.clear);
        }else{
            textTitle.setText(R.string.game_over);
        }

        textTileCount.setText(getString(R.string.tile_count,tileCount));

        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClearActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


    }

}
