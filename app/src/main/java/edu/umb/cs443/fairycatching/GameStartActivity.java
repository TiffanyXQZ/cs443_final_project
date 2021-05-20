package edu.umb.cs443.fairycatching;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class GameStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);

        Spinner spinner1 = (Spinner) findViewById(R.id.spFairySpeed);
        Spinner spinner2 = (Spinner) findViewById(R.id.spHunterSpeed);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.character_speed_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner2.setSelection(2);


    }

    public void onGameStart(View v){
        Intent intent = new Intent(GameStartActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
