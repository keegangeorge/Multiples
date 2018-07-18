package com.kgeor.keegangeorge_a3;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MainActivity class for Random Numbers application
 * Implements touch, drag, and click listener interfaces
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

    // METHODS //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // CHECKS NETWORK CONNECTION AND SHARES RESULTS //
        checkConnection();

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


    /**
     * Method responsible for checking if device is connected to a network and will display a toast
     * accordingly in addition to the type of network being used.
     */
    public void checkConnection() {
        /*
         * References the network connectivity service to receive information regarding the devices
         * network connectivity state
         */
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        /*
         * When network information is present and connected, check for network type and reveal
         * results in a toast message
         */
        if (networkInfo != null && networkInfo.isConnected()) {
            String networkInformation = networkInfo.getTypeName();
            Toast.makeText(this, "CONNECTED TO: " + networkInformation, Toast.LENGTH_SHORT).show();

            /*
             * When there is no network connection present, indicate results to user
             */
        } else {
            Toast.makeText(this, "NETWORK CONNECTION UNAVAILABLE", Toast.LENGTH_LONG).show();
        }
    } // checkConnection() method end

    /**
     * Method responsible for handling device click events
     *
     * @param v the current view being clicked
     */
    @Override
    public void onClick(View v) {
        /*
         * Checks view id's to see what button is being clicked on and executes commands based on
         * the resulting view
         */
        switch (v.getId()) {
            // Fetches random numbers from specified link
            case R.id.retrieve_random_num:
                new getRandomNum(this).execute(FETCHED_LINK);
                break;
            // Resets text to placeholder values and colors to initial values
            case R.id.btn_clear:
                randomText1.setText(R.string.str_numPlaceholder);
                randomText2.setText(R.string.str_numPlaceholder);
                randomText3.setText(R.string.str_numPlaceholder);
                randomText4.setText(R.string.str_numPlaceholder);

                multipleOfTwo.setText(R.string.str_multiple_2);
                multipleOfThree.setText(R.string.str_multiple_3);
                multipleOfTen.setText(R.string.str_multiple_10);
                multipleOfFive.setText(R.string.str_multiple_5);

                randomButtons[0].setBackgroundColor(getResources().getColor(R.color.colorTextViewBG));
                randomButtons[1].setBackgroundColor(getResources().getColor(R.color.colorTextViewBG));
                randomButtons[2].setBackgroundColor(getResources().getColor(R.color.colorTextViewBG));
                randomButtons[3].setBackgroundColor(getResources().getColor(R.color.colorTextViewBG));
        }
    } // onClick() method end

    /**
     * Method responsible for handling drag events
     *
     * @param v     current view being dragged
     * @param event state/type of drag action
     * @return true if being dragged / false if not dragged
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onDrag(View v, DragEvent event) {
        /*
         * Checks the type of drag action and executes commands based on the drag event
         */
        switch (event.getAction()) {
            // When dragging into the card, highlight the card (display highlighted card layout)
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.card_highlighted));
                break;
            // When not/finished dragging, return the card color to default state (display normal card)
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.card_normal));
                break;
            /* When text is dropped on the view (card), if the correct number is placed append
             * the number to the text on the card
             */
            case DragEvent.ACTION_DROP:
                // current card
                View view = (View) event.getLocalState();
                TextView itemDropped = (TextView) view;

                // get the string of the fetched integer value
                int fetchedValue = Integer.valueOf((String) itemDropped.getText());
                int multipleOf = -1;
                // check the multiple values based on the card
                if (v.getId() == R.id.card_multiple_2) {
                    multipleOf = 2;
                } else if (v.getId() == R.id.card_multiple_3) {
                    multipleOf = 3;
                } else if (v.getId() == R.id.card_multiple_5) {
                    multipleOf = 5;
                } else if (v.getId() == R.id.card_multiple_10) {
                    multipleOf = 10;
                }
                /*
                 * When the fetched value is the multiple of a card append the fetched value as a
                 * string to the text on the card
                 */
                if (multipleOf > 0) {
                    if (fetchedValue % multipleOf == 0) {
                        String cardText = (String) ((TextView) v).getText();
                        cardText = cardText + "\n" + fetchedValue;
                        ((TextView) v).setText(cardText);
                        // if fetched value is a multiple of the card, allow it to be dropped
                        itemDropped.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });
                        // remove the fetched number text on the text view box
                        itemDropped.setText("");

                    }
                }
                // remove highlight of card and return card to default colors
                v.setBackground(ContextCompat.getDrawable(this, R.drawable.card_normal));
                break;
        }
        return true;
    }

    /**
     * Responsible for the devices touch events
     *
     * @param v     the current view being touched
     * @param event the state/type of touch being executed
     * @return true if touch executed, false if not touched
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // if text view is touched, create a shadow and initiate drag
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData clip = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
            v.startDrag(clip, shadow, v, 0);
            return true;
        }
        return false;
    }

    /**
     * Method responsible for reading the JSON data from the specified URL
     *
     * @param myUrl specified url linking to the JSON data file
     * @return returns the String of JSON data
     */
    public String readJSONData(String myUrl) throws IOException {
        InputStream inputStream = null;
        // displays only the first 500 characters retrieved from the web page
        int length = 2500;

        /* obtains a new HttpURLConnection by opening the connection and casts the
         * result to HttpURLConnection
         */
        URL url = new URL(myUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // prepares the connection request
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
            String streamToString = readStream(inputStream, length);
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
     * @param stream the current input stream
     * @param len    the length of the stream
     * @return the String value of the stream
     */
    public String readStream(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /**
     * Inner AsyncTask class for handling the fetching of the random
     * number in an asynchronous thread
     */
    private class getRandomNum extends AsyncTask<String, Void, String> {
        // FIELDS //
        Exception exception = null;
        View.OnTouchListener listener;

        getRandomNum(View.OnTouchListener listen) {
            listener = listen;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return readJSONData(urls[0]);
            } catch (IOException e) {
                exception = e;
            }
            return null;
        }

        /**
         * Takes the JSON Result as a string and parses it
         *
         * @param result the JSON result that is parsed
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String result) {
            try {
                // creates new JSON object with key/value mapping from the JSON string
                JSONObject jsonObj = new JSONObject(result);
                JSONArray randomArray = jsonObj.getJSONArray("data");

                String toastMsg = "RETRIEVED NUMBERS: ";
                /*
                 * Loop through the JSON Array and get each integer values and
                 * set the integers as text in the text view
                 */
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
                        randomButtons[i].setBackgroundColor(getResources().getColor(R.color.colorInactive));
                        randomButtons[i].setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                    } else {
                        randomButtons[i].setBackgroundColor(getResources().getColor(R.color.colorTextViewBG));
                    }
                }
                toastMsg = toastMsg.substring(0, toastMsg.length() - 1);
                Toast.makeText(getBaseContext(), toastMsg, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // When retrieval of integers fails:
                Log.d("getRandomNum", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(), "Retrieval Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

} // MainActivity class end




