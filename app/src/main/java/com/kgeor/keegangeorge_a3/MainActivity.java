package com.kgeor.keegangeorge_a3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {

    // Variables for storing GUI elements //
    TextView randomText1, randomText2, randomText3, randomText4;
    TextView[] randomButtons;
    TextView multipleOfTwo, multipleOfThree, multipleofTen, multipleofFive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // LINKING TEXT VIEW REFERENCES: XML TO JAVA //
        randomText1 = findViewById(R.id.btn_random_1);
        randomText2 = findViewById(R.id.btn_random_2);
        randomText3 = findViewById(R.id.btn_random_3);
        randomText4 = findViewById(R.id.btn_random_4);

        // ASSOCIATING TEXT VIEWS WITH RANDOM BUTTONS //
        randomButtons = new TextView[4];
        randomButtons[0] = randomText1;
        randomButtons[1] = randomText2;
        randomButtons[2] = randomText3;
        randomButtons[3] = randomText4;

        // LINKING BUTTON REFERENCES: XML TO JAVA //
        Button btnRandom = findViewById(R.id.retrieve_random_num);
        Button btnClear = findViewById(R.id.btn_clear);

        // LINK CARD REFERENCES: XML TO JAVA //
        multipleOfTwo = findViewById(R.id.card_multiple_2);
        multipleOfThree = findViewById(R.id.card_multiple_3);
        multipleOfTen = findViewById(R.id.card_multiple_10);
        multipleOfFive = findViewById(R.id.card_multiple_5);

        // DRAG LISTENERS FOR CARDS //
        multipleOfTwo.setOnDragListener(this);
        multipleOfThree.setOnDragListener(this);
        multipleOfTen.setOnDragListener(this);
        multipleOfFive.setOnDragListener(this);

        // BUTTON CLICK LISTENERS FOR TOP BUTTONS //
        btnRandom.setOnClickListener(this);
        btnClear.setOnClickListener(this);

    } // end onCreate()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retrieve_random_num:
                break;
            case R.id.btn_clear:
                randomText1.setText(R.string.str_numPlaceholder);
                randomText2.setText(R.string.str_numPlaceholder);
                randomText3.setText(R.string.str_numPlaceholder);
                randomText4.setText(R.string.str_numPlaceholder);

                multipleOfTwo.setText(R.string.str_multiple_2);
                multipleOfThree.setText(R.string.str_multiple_3);
                multipleOfTen.setText(R.string.str_multiple_10);
                multipleOfFive.setText(R.string.str_multiple_5);
        }

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
