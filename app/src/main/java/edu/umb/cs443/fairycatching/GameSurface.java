package edu.umb.cs443.fairycatching;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;

    private final List<GameCharacter> charactersList = new ArrayList<GameCharacter>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();
    private Bitmap bgImage ;

    private static final int MAX_STREAMS=100;
    private int soundIdExplosion;
    private int soundIdBackground;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    public GameSurface(Context context)  {
        super(context);
        this.setFocusable(true);            // set focusable to control events on the surface.
        this.getHolder().addCallback(this);
        this.initSoundPool();
    }

    //Setting up background sound and explosion sound
    private void initSoundPool()  {
        //requires Android API >= 21
        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool.Builder builder= new SoundPool.Builder();
        builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);
        this.soundPool = builder.build();

        //SoundPool has been loaded
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
                playSoundBackground(); // background sound
            }
        });
        this.soundIdBackground= this.soundPool.load(this.getContext(), R.raw.bgsound,1);
        this.soundIdExplosion = this.soundPool.load(this.getContext(), R.raw.explosion,1);
    }

    public void playSoundExplosion()  {
        if(this.soundPoolLoaded) {
            //explosion.wav
            int streamId = this.soundPool.play(this.soundIdExplosion,
                                                0.6f, 0.6f, 1, 0, 1f);
        }
    }

    public void playSoundBackground()  {
        if(this.soundPoolLoaded) {
            int streamId = this.soundPool.play(this.soundIdBackground,
                    1, 1, 1, -1, 1f);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( charactersList.size()==0 )
            return false;

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //Change moving direction of catcher (the first item in charactersList)
            int movingVectorX = x - charactersList.get(0).getX();
            int movingVectorY = y - charactersList.get(0).getY();
            charactersList.get(0).setMovingVector(movingVectorX, movingVectorY);

            /*
            //destroy the object if it is a fairy
            Iterator<GameCharacter> iterator= this.charactersList.iterator();
            while(iterator.hasNext()) {
                GameCharacter character = iterator.next();

                //skip the catcher
                if(character == charactersList.get(0))
                    continue;

                if( character.getX() < x && character.getY() < y &&
                        x < character.getX() + character.getWidth() &&
                        y < character.getY()+ character.getHeight()) {
                    iterator.remove();

                    // Create an explosion object at the position of fairy
                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap,character.getX(),character.getY());
                    this.explosionList.add(explosion);
                }
            }
             */

            return true;
        }
        return false;
    }

    public void update()  {
        //moves the characters to new position
        for(GameCharacter character: charactersList) {
            character.update();
        }

        //then check if any fairy is catch by the hunter
        GameCharacter theCatcher = charactersList.get(0);
        Iterator<GameCharacter> it= this.charactersList.iterator();
        while(it.hasNext()) {
            GameCharacter f = it.next();
            if (f == theCatcher) continue;

            if (f.isBeKilled() == false && theCatcher.isOverlapping(f) == true) {
                f.setMovingAnimation(false);
                f.setMovingVector(0, 0); //stop moving the fairy
                f.toBeKiled();

                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.explosion);
                Explosion explosion = new Explosion(this, bitmap,f.getX(),f.getY());
                this.explosionList.add(explosion);
                it.remove();
            }
         }

        //update the explosion
        for(Explosion explosion: this.explosionList)  {
            explosion.update();
        }

        Iterator<Explosion> iterator= this.explosionList.iterator();
        while(iterator.hasNext())  {
            Explosion explosion = iterator.next();
            if(explosion.isFinish()) {
                iterator.remove();
            }
        }

        //If there are not any fairies, then stop moving the hunter
        if(charactersList.size() < 2) {
            charactersList.get(0).setMovingVector(0, 0);
            charactersList.get(0).setMovingAnimation(false);
        }
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);

        //draw the background image
        canvas.drawBitmap(bgImage, 0, 0, null); // draw the background

        //landscape --> draw second background on the right
        //portrait --> draw second background on the bottom
        if (getHolder().getSurfaceFrame().width() > getHolder().getSurfaceFrame().height())
            canvas.drawBitmap(bgImage, bgImage.getWidth(), 0, null);
        else
            canvas.drawBitmap(bgImage, 0, bgImage.getHeight(), null);


        //draw characters
        for(GameCharacter character: charactersList)  {
            character.draw(canvas);
        }

        //draw explosion objects
        for(Explosion explosion: this.explosionList)  {
            explosion.draw(canvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //setting up background image
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        float scale = (float)background.getHeight()/(float)getHeight();
        int newWidth = Math.round(background.getWidth()/scale);
        int newHeight = Math.round(background.getHeight()/scale);
        this.bgImage = Bitmap.createScaledBitmap(background, newWidth, newHeight, true);

        //-----------generate CATCHER object
        Bitmap catcherBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.catcher2);
        GameCharacter theCatcher = new GameCharacter(this,catcherBitmap,
                    getHolder().getSurfaceFrame().width(), 50);
        theCatcher.setVelocity(.2f);
        theCatcher.setMovingVector(0,0);

        //------------generate FAIRY objects at random spots
        Bitmap fairyBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.fairy);
        this.charactersList.add(theCatcher);

        int screen_width = getHolder().getSurfaceFrame().width();
        int screen_height = getHolder().getSurfaceFrame().height();
        for(int i=0; i<10; i++){
            Random r = new Random();
            int x = r.nextInt(screen_width);
            int y = r.nextInt(screen_width);
            GameCharacter fairy = new GameCharacter(this, fairyBitmap, x, y);
            this.charactersList.add(fairy);
        }

        //create a game thread
        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    // interface SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //return the game thread
    public GameThread getThread() {
        return gameThread;
    }

    // interface SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);
                this.gameThread.join(); // main thread must be stop to wait GameThread.
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }
}

