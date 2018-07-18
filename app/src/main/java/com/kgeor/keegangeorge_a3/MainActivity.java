package com.kgeor.keegangeorge_a3;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MainActivity class for Random Numbers application
 *
 * @author Keegan George
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {
    // FIELDS //
    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final String FETCHED_LINK = "https://qrng.anu.edu.au/API/jsonI.php?length=4&type=uint8";

    // Variables for storing GUI elements //
    TextView randomText1, randomText2, randomText3, randomText4;
    TextView[] randomButtons;
    TextView multipleOfTwo, multipleOfThree, multipleOfTen, multipleOfFive;


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
                new getRandomNum(this).execute(FETCHED_LINK);
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
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.card_highlighted));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.card_normal));
                break;
            case DragEvent.ACTION_DROP:
                View view = (View) event.getLocalState();
                TextView itemDropped = (TextView) view;
                int fetchedValue = Integer.valueOf((String) itemDropped.getText());
                int multipleOf = -1;
                if (v.getId() == R.id.card_multiple_2) {
                    multipleOf = 2;
                } else if (v.getId() == R.id.card_multiple_3) {
                    multipleOf = 3;
                } else if (v.getId() == R.id.card_multiple_5) {
                    multipleOf = 5;
                } else if (v.getId() == R.id.card_multiple_10) {
                    multipleOf = 10;
                }

                if (multipleOf > 0) {
                    if (fetchedValue % multipleOf == 0) {
                        String cardText = (String) ((TextView) v).getText();
                        cardText = cardText + "\n" + fetchedValue;
                        ((TextView) v).setText(cardText);

                        itemDropped.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });
                        itemDropped.setText("");

                    }
                }
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.card_normal));
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData clip = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
            v.startDrag(clip, shadow, v, 0);
            return true;
        }
        return false;
    }


    public String readJSONData(String myUrl) throws IOException {
        InputStream inputStream = null;
        int len = 2500;

        URL url = new URL(myUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(1500);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // Begin Query:
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "THE REQUESTED RESPONSE IS: " + response);
            inputStream = connection.getInputStream();

            // Convert InputStream to String:
            String streamToString = readStream(inputStream, len);
            return streamToString;

            // Ensures the closing of InputStream after finishing using it in the application:
        } finally {
            if (inputStream != null) {
                inputStream.close();
                connection.disconnect();
            }
        }

    }

    /**
     * Reads an InputStream and converts it to a String
     *
     * @param stream
     * @param len
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public String readStream(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    // Inner Async Class:

    private class getRandomNum extends AsyncTask<String, Void, String> {

        Exception exception = null;
        View.OnTouchListener listener;

        getRandomNum(View.OnTouchListener listen) {
            listener = listen;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObj = new JSONObject(s);
                JSONArray randomArray = jsonObj.getJSONArray("data");

                String toastMsg = "";

                for (int i = 0; i < randomArray.length(); i++) {
                    int val = (Integer) randomArray.get(i);
                    toastMsg = toastMsg + val + ", ";
                    randomButtons[i].setText("" + val);
                    randomButtons[i].setOnTouchListener(listener);

                    /*
                     * If retrieved value is not a multiple of 2, 3, 5, or 10 then
                     * prevent touching/dragging the TextView
                     */
                    if (val % 2 != 0 && val % 3 != 0 && val % 5 != 0 && val % 10 != 0) {
                        randomButtons[i].setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                    }
                }
                toastMsg = toastMsg.substring(0, toastMsg.length() - 1);
                Toast.makeText(getBaseContext(), toastMsg, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.d("getRandomNum", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(), "Retrieval Failed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return readJSONData(strings[0]);
            } catch (IOException e) {
                exception = e;
            }
            return null;
        }


    }


} // MainActivity class end




