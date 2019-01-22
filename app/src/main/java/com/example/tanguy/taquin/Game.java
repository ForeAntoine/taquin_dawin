package com.example.tanguy.taquin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class Game extends Activity implements GameView.EndGameListener {

    private int PICK_IMAGE_REQUEST = 1;
    private int width;
    private int height;
    private Uri uriimage = null;

    private final Stopwatch gametime = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.uriimage = getIntent().getData();
        int size = getIntent().getIntExtra("size", 3);
        this.width = size;
        this.height = size;
        ImageView imageView = (ImageView) findViewById(R.id.puzzle);
        Bitmap bitmap=null;
        try {
            if (this.uriimage == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.putin);
                Log.i("test", "putin");
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), this.uriimage);
                Log.i("test", "capibara");
            }

            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GameView game = new GameView(this, bitmap, size, size);
        game.setEndGameListener(this);
        setContentView(game);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gametime.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gametime.start();
    }

    @Override
    public void onGameEnded(int moves)
    {
        Intent end_game = new Intent();
        end_game.setClass(this, EndGame.class);
        end_game.setData(uriimage);
        end_game.putExtra("width", width);
        end_game.putExtra("height", height);
        end_game.putExtra("time", gametime.milliseconds());
        end_game.putExtra("moves", moves);
        startActivity(end_game);
        finish();
    }
}
