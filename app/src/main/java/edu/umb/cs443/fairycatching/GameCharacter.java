package edu.umb.cs443.fairycatching;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class GameCharacter extends GameObject {
    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;


    // row of using images.
    private int rowUsing = ROW_LEFT_TO_RIGHT;
    private int colUsing;
    private boolean isMovingAnimation = true;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    private float velocity = 0.15f;     // moving speed (pixel/milisecond).
    private int movingVectorX = 10;     //default moving vectorX
    private int movingVectorY = 5;      //default moving vectorY
    private long lastDrawNanoTime =-1;

    private GameSurface gameSurface;

    public void setVelocity(float v){ velocity = v; }
    public float getVelocity(){ return velocity; }

    public void setMovingAnimation(boolean m){
        isMovingAnimation = m;
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        this.lastDrawNanoTime= System.nanoTime();
    }

    public GameCharacter(GameSurface gameSurface, Bitmap image, int x, int y) {
        super(image, 4, 3, x, y);

        this.gameSurface= gameSurface;
        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }

        //generate random moving vector
        Random r = new Random();
        movingVectorX = r.nextInt(50);     //default moving vectorX
        movingVectorY = r.nextInt(50);     //default moving vectorY
    }

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }

    public void update()  {
        if(isMovingAnimation == false)
            return;

        this.colUsing++;
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }

        long now = System.nanoTime();
        if(lastDrawNanoTime==-1) { // If
            lastDrawNanoTime= now;
        }

        //1 nanosecond = 10^6 millisecond.
        int deltaTime = (int) ((now - lastDrawNanoTime)/1000000);

        float distance = velocity * deltaTime; // the distance of moving(pixel).
        double movingVectorLength = Math.sqrt(movingVectorX* movingVectorX + movingVectorY*movingVectorY);

        // new position of character.
        this.x = x +  (int)(distance* movingVectorX / movingVectorLength);
        this.y = y +  (int)(distance* movingVectorY / movingVectorLength);

        // change moving direction when the character reaches to edge of screen
        if(this.x < 0 )  {
            this.x = 0;
            this.movingVectorX = - this.movingVectorX;
        } else if(this.x > this.gameSurface.getWidth() -width)  {
            this.x= this.gameSurface.getWidth()-width;
            this.movingVectorX = - this.movingVectorX;
        }

        if(this.y < 0 )  {
            this.y = 0;
            this.movingVectorY = - this.movingVectorY;
        } else if(this.y > this.gameSurface.getHeight()- height)  {
            this.y= this.gameSurface.getHeight()- height;
            this.movingVectorY = - this.movingVectorY ;
        }

        // calculate row of using image.
        if( movingVectorX > 0 )  {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX= movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    //helper for isOverlapping function
    private  boolean valueInRange(int value, int min, int max) {
        return (value > min) && (value < max);
    }

    //check if two characters is overlapped
    public boolean isOverlapping(GameCharacter other){
        boolean xOverlap = valueInRange(this.x, other.x, other.x + other.width) ||
                            valueInRange(other.x, this.x, this.x + this.width);
        boolean yOverlap = valueInRange(this.y, other.y, other.y + other.height) ||
                            valueInRange(other.y, this.y, this.y + this.height);

        return xOverlap && yOverlap;
    }
}
