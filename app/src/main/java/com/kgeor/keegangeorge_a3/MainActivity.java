package com.kgeor.keegangeorge_a3;

import android.annotation.TargetApi;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    public String readJSONData(String myUrl) throws IOException {
        InputStream inptStream = null;
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
            inptStream = connection.getInputStream();

            // Convert InputStream to String:
            String streamToString = readStream(inptStream, len);
            return streamToString;

            // Ensures the closing of InputStream after finishing using it in the application:
        } finally {
            if (inptStream != null) {
                inptStream.close();
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

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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




