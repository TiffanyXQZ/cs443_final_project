package edu.umb.cs443.fairycatching;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    static final int REFRESH_RATE = 100;

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.gameSurface= gameSurface;
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run()  {
        long startTime = System.nanoTime();
        while(running)  {
            Canvas canvas= null;
            try {
                canvas = this.surfaceHolder.lockCanvas();           // lock the Canvas
                synchronized (canvas)  {
                    this.gameSurface.update();
                    this.gameSurface.draw(canvas);
                }
            }finally {
                if(canvas!= null)  {
                    this.surfaceHolder.unlockCanvasAndPost(canvas);   // unlock the Canvas.
                }
            }

            // time to update Game interface
            long now = System.nanoTime() ;
            long waitTime = (now - startTime)/1000000;
            if(waitTime < REFRESH_RATE)  {
                waitTime = REFRESH_RATE; // Millisecond.
            }
            //System.out.print(" Wait Time="+ waitTime);
            try {
                this.sleep(waitTime); // stop for a while.
            } catch(InterruptedException e)  {
                //do nothing here
            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
    }

    public void setRunning(boolean running)  {
        this.running=running;
    }

    public void doDraw(Canvas canvas){

    }
}
