package com.example.tanguy.taquin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class GameView extends View {

    private class Block {
        int x;
        int y;
        Bitmap bitmap;
    }

    public interface EndGameListener {
        void onGameEnded(int moves);
    }

    private int width, height;
    private Block[] listBlocks = null;
    private int currentBlock = -1;
    private int [][] gameGrid = null;
    private int emptyX, emptyY;
    private long animBegan;
    private final Paint thePaint = new Paint();
    private int TileSizeX;
    private int TileSizeY;
    private float tileAspect;
    private EndGameListener endGameListener;
    private int nbMoves = 0;

    public GameView(Context context, Bitmap image, int pWidth, int pHeight) {
        super(context);
        this.width = pWidth;
        this.height = pHeight;

        //preparation de la grille
        gameGrid = new int[this.width][this.height];
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                if (y == this.height - 1 && x == this.width - 1) {
                    gameGrid[x][y] = -1;
                } else {
                    gameGrid[x][y] = y*this.width + x;
                }
            }
            emptyX = this.width -1;
            emptyY = this.height -1;
        }

        // Melange de la grille
        Random rand = new Random();
        int[][] dirs = {{1,0},{0,1},{-1,0},{0,-1}};
        for (int m = 0; m < 1000; m++) {
            int dir = rand .nextInt(4);
            int chx = emptyX + dirs[dir][0];
            int chy = emptyY + dirs[dir][1];
            if (0 <= chx && chx < this.width && 0 <= chy && chy < this.height ) {
                gameGrid[emptyX][emptyY] = gameGrid[chx][chy];
                emptyX = chx;
                emptyY = chy;
                gameGrid[emptyX][emptyY] = -1;
            }
        }

        //Creation des blocks
        int w = image.getWidth()/this.width;
        int h = image.getHeight()/this.height;
        tileAspect = (1.f*h)/w;
        listBlocks = new Block[this.width*this.height - 1];
        for(int y = 0; y < this.height; ++y)
            for(int x = 0; x < this.width; ++x)
            {
                int id = gameGrid[x][y];
                if(id != -1)
                {
                    listBlocks[id] = new Block();
                    listBlocks[id].x = x;
                    listBlocks[id].y = y;
                    int iy = id / this.width;
                    int ix = id % this.width;
                    listBlocks[id].bitmap = Bitmap.createBitmap(image, ix*w, iy*h, w, h);
                }
            }

        // Changement des tailles des tiles
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    public void setEndGameListener(EndGameListener listener)
    {
        endGameListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        if(tileAspect != 0)
        {
            float tileSizeX = w/ width;
            float tileSizeY = h/ height;
            TileSizeX = (int)Math.floor(Math.min(tileSizeX, tileSizeY/ tileAspect));
            TileSizeY = (int)Math.floor(Math.min(tileSizeX* tileAspect, tileSizeY));
        }
    }

    private void drawBlock(Canvas canvas, float x, float y, Bitmap bitmap, int bordercolor)
    {
        float rx = x* TileSizeX;
        float ry = y* TileSizeY;
        canvas.drawBitmap(bitmap, null, new RectF(rx, ry, rx+ TileSizeX, ry+ TileSizeY), thePaint);

        int x1 = Math.round((x  ) * TileSizeX) + 2;
        int x2 = Math.round((x+1) * TileSizeX) - 2;
        int y1 = Math.round((y  ) * TileSizeY) + 2;
        int y2 = Math.round((y+1) * TileSizeY) - 2;
        float[] points = {x1, y1, x2, y1,
                x2, y1, x2, y2,
                x2, y2, x1, y2,
                x1, y2, x1, y1
        };
        thePaint.setColor(bordercolor);
        canvas.drawLines(points, thePaint);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(listBlocks != null)
        {
            // Animation du block actif
            if(currentBlock != -1 && SystemClock.uptimeMillis() - animBegan < 200)
            {
                int start_x, start_y;
                start_x = listBlocks[currentBlock].x;
                start_y = listBlocks[currentBlock].y;
                float x = start_x + (emptyX - start_x) * (SystemClock.uptimeMillis() - animBegan)/200.f;
                float y = start_y + (emptyY - start_y) * (SystemClock.uptimeMillis() - animBegan)/200.f;

                drawBlock(canvas, x, y,
                        listBlocks[currentBlock].bitmap, 0xFFFF0000);
                invalidate();
            }
            // Fin animation
            else if(currentBlock != -1)
            {
                int old_x = listBlocks[currentBlock].x;
                int old_y = listBlocks[currentBlock].y;
                listBlocks[currentBlock].x = emptyX;
                listBlocks[currentBlock].y = emptyY;
                emptyX = old_x;
                emptyY = old_y;
                gameGrid[emptyX][emptyY] = -1;
                gameGrid[listBlocks[currentBlock].x][listBlocks[currentBlock].y] = currentBlock;
                currentBlock = -1;
                nbMoves++;
                checkEnd();
            }

            // Affichage des autres blocks
            for(int i = 0; i < listBlocks.length; ++i)
            {
                if(currentBlock == i)
                    continue;
                drawBlock(canvas, listBlocks[i].x, listBlocks[i].y,
                        listBlocks[i].bitmap, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            // trouver le bon block
            int x = Math.round(event.getX());
            int y = Math.round(event.getY());
            if(x < width * TileSizeX && y < height * TileSizeY)
            {
                int u = (int)Math.floor(x/ TileSizeX);
                int v = (int)Math.floor(y/ TileSizeY);
                // Si le block peut bouger
                if(currentBlock == -1
                        && Math.abs(u- emptyX) + Math.abs(v- emptyY) == 1)
                {
                    currentBlock = gameGrid[u][v];
                    animBegan = SystemClock.uptimeMillis();
                    GameView.this.invalidate();
                    return true;
                }
            }
        }
        return false;
    }

    private void checkEnd()
    {
        for(int y = 0; y < height; ++y)
            for(int x = 0; x < width; ++x)
            {
                if(x == width -1 && y == height -1)
                    continue;
                if(gameGrid[x][y] != y* width + x)
                    return ;
            }
        endGameListener.onGameEnded(nbMoves);
    }

}
