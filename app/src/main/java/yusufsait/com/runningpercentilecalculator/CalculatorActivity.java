package yusufsait.com.runningpercentilecalculator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

public class CalculatorActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private AdView mAdView;
    String distance = "5k";
    String gender = "Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userTheme = preferences.getString("prefTheme", "darkab");
        if (userTheme.equals("default"))
            setTheme(R.style.AppTheme);
        else if (userTheme.equals("Green"))
            setTheme(R.style.AppTheme);
        else if (userTheme.equals("Mint"))
            setTheme(R.style.AppTheme);
        else if (userTheme.equals("Grey"))
            setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calculate Percentile");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_about:
                                Intent aboutIntent = new Intent(CalculatorActivity.this, AboutActivity.class);
                                startActivity(aboutIntent);
                                break;
                            case R.id.nav_rate:
                                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                // To count with Play market backstack, After pressing back button,
                                // to taken back to our application, we need to add following flags to intent.
                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                }
                                break;
                            case R.id.nav_feedback:
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                String[] strings = new String[]{"yusufsaitappfeedback@gmail.com"};
                                intent.putExtra(Intent.EXTRA_EMAIL, strings);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                                break;
                            case R.id.nav_more:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/dev?id=5782570041305794849")));
                        }
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });
        Spinner distanceSpinner = findViewById(R.id.distance);
        ArrayAdapter<CharSequence> distanceAdapter = ArrayAdapter.createFromResource(this, R.array.distance, android.R.layout.simple_spinner_item);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(distanceAdapter);

        Spinner genderSpinner = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        EditText editText = (EditText) findViewById(R.id.second);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    onClickCalculateButton(v);
                    handled = true;
                }
                return handled;
            }
        });
        /*mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onClickCalculateButton(View view){
        TextView percentileTextView = findViewById(R.id.percentile);
        TextView speedTextView = findViewById(R.id.speed);
        TextView allPercentileTextView = findViewById(R.id.all_percentile);
        TextView textView5 = findViewById(R.id.textView5);
        Spinner genderSpinner = findViewById(R.id.gender);
        EditText hoursEditText = findViewById(R.id.hour);
        EditText minutesEditText = findViewById(R.id.minute);
        EditText secondsEditText = findViewById(R.id.second);

        if(hoursEditText.getText().length() ==0 && minutesEditText.getText().length() == 0 && secondsEditText.getText().length() ==0) {
            Toast.makeText(this,"Please enter a time",Toast.LENGTH_SHORT).show();
        }
        else {
            int time = getTimeInSeconds();


            gender = genderSpinner.getSelectedItem().toString();
            String sql = "SELECT * FROM " + gender;
            int percentile = getPercentile(time, sql);
            Log.e("percentile", Integer.toString(percentile));
            double speed = 0;
            if (distance.equals("5k")) {
                speed = 5 / ((double) time / (60 * 60));
            } else if (distance.equals("10k")) {
                speed = 10 / ((double) time / (60 * 60));
            } else if (distance.equals("half_marathon")) {
                speed = 21.0975 / ((double) time / (60 * 60));
            } else if (distance.equals("marathon")) {
                speed = 42.195 / ((double) time / (60 * 60));
            }

            int allPercentile = getPercentile(time, "SELECT * FROM Alll");

            percentileTextView.setText(Integer.toString(percentile) + "%");
            allPercentileTextView.setText(Integer.toString(allPercentile) + "%");
            speedTextView.setText(String.format("%.2f", speed) + " km/hr");
            textView5.setText("Percentile of " + gender.toLowerCase() + " runners:");
        }
    }

    private int getTimeInSeconds(){
        EditText hoursEditText = findViewById(R.id.hour);
        EditText minutesEditText = findViewById(R.id.minute);
        EditText secondsEditText = findViewById(R.id.second);
        int hours= 0, minutes = 0, seconds = 0;
        if(!hoursEditText.getText().toString().equals("")){
            hours = Integer.parseInt(hoursEditText.getText().toString()) * 60 * 60;
        }
        if(!minutesEditText.getText().toString().equals("")){
            minutes = Integer.parseInt(minutesEditText.getText().toString())*60;
        }
        if(!secondsEditText.getText().toString().equals("")){
            seconds = Integer.parseInt(secondsEditText.getText().toString());
        }
        return hours + minutes + seconds;
    }
    private int getPercentile(int runningTime, String sql){
        MyDataBase runningDatabase = new MyDataBase(this);
        SQLiteDatabase runningSQLDatabase = runningDatabase.getReadableDatabase();


        Cursor cursor = runningSQLDatabase.rawQuery(sql,null);
        HashMap<Integer,Integer> hashMap = new HashMap<>();

        Spinner distanceSpinner = findViewById(R.id.distance);
        String text = distanceSpinner.getSelectedItem().toString();

        if(text.equals("5 Kilometers")){
            distance = "5k";
        }
        else if(text.equals("10 Kilometers")){
            distance = "10k";
        }
        else if(text.equals("Half Marathon")){
            distance = "half_marathon";
        }
        else if(text.equals("Marathon")){
            distance = "marathon";
        }

        String timeColumn = distance + "_time";
        while(cursor.moveToNext()){
            int percentile = cursor.getInt(cursor.getColumnIndexOrThrow("percentile"));
            int time = cursor.getInt(cursor.getColumnIndexOrThrow(timeColumn));
            hashMap.put(percentile,time);
        }
        int runningPercentile = 1;
        int runningTimeDifference = 99999999;
        for(Map.Entry<Integer,Integer> entry: hashMap.entrySet()){
            int difference = Math.abs(runningTime - entry.getValue());
            if(difference < runningTimeDifference){
                runningTimeDifference = difference;
                runningPercentile = entry.getKey();
            }
        }
        cursor.close();
        return 100-runningPercentile;
    }

}
