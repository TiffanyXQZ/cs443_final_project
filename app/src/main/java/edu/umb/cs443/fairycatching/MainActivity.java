package edu.umb.cs443.fairycatching;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    GameSurface gView;
    GameThread gThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);        // Set Fullscreen mode
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);         // Remove the title of this app

        gView = new GameSurface(this);
        setContentView(gView);
    }
}
