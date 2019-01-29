package com.example.tanguy.taquin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private Uri image = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Charge une image par defaut
        ImageView viewImage = findViewById(R.id.imagePreview);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.putin);
        viewImage.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Recupere l'image charger dans l'imageview
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            this.image = uri;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = findViewById(R.id.imagePreview);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void buttonLoad(View view) {
        // Ouvre la galerie pour charger une image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void scores(View view) {
    }

    public void gameLoad(View view) {
        // Charge le jeu avec la taille de grille selectionner
        Intent game = new Intent(MainActivity.this, Game.class);
        //getIntent().putExtra("image", this.image);
        int size = Integer.parseInt((((Button) view).getText().toString()).substring(0, 1));
        Log.i("size", (((Button) view).getText().toString()).substring(0, 1));
        game.putExtra("size", size);
        game.setData(this.image);
        startActivity(game);

    }
}
