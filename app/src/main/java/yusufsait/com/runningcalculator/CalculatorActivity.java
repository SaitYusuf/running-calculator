package yusufsait.com.runningcalculator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class CalculatorActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    String distance = "5k";
    String gender = "Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Calculate");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }


        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String distanceSpinnerText = parent.getItemAtPosition(position).toString();
                switch (distanceSpinnerText) {
                    case "5 Kilometers":
                        distance = "5k";
                        break;
                    case "10 Kilometers":
                        distance = "10k";
                        break;
                    case "Half Marathon":
                        distance = "half_marathon";
                        break;
                    case "Marathon":
                        distance = "marathon";
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                gender = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        EditText editText = findViewById(R.id.second);
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
    }

    public void onClickCalculateButton(View view){
        TextView percentileTextView = findViewById(R.id.percentile);
        TextView paceTextView = findViewById(R.id.pace);
        TextView allPercentileTextView = findViewById(R.id.all_percentile);
        TextView ageGradeTextView = findViewById(R.id.age_grade);
        TextView textView5 = findViewById(R.id.textView5);
        Spinner genderSpinner = findViewById(R.id.gender);
        EditText hoursEditText = findViewById(R.id.hour);
        EditText minutesEditText = findViewById(R.id.minute);
        EditText secondsEditText = findViewById(R.id.second);
        EditText ageEditText = findViewById(R.id.age);


        if(hoursEditText.getText().length() ==0 && minutesEditText.getText().length() == 0 && secondsEditText.getText().length() ==0) {
            Toast.makeText(this,"Please enter a time",Toast.LENGTH_SHORT).show();
            return;
        }
        if(ageEditText.getText().length() == 0){
            Toast.makeText(this,"Please enter age",Toast.LENGTH_SHORT).show();
            return;
        }
        if(Integer.parseInt(ageEditText.getText().toString()) < 5 || Integer.parseInt(ageEditText.getText().toString()) > 100){
            Toast.makeText(this,"Please enter an age between 5 and 100",Toast.LENGTH_SHORT).show();
            return;
        }
        int time = getTimeInSeconds();
        int percentile = getPercentile("SELECT * FROM " + gender);
        int allPercentile = getPercentile("SELECT * FROM Alll");
        int ageGrade = getAgeGrade(Integer.parseInt(ageEditText.getText().toString()));
        String pace = "";
        switch (distance) {
            case "5k":
                int pace5k = (int) Math.round(time/5);
                pace = (pace5k/60) + ":" + String.format("%02d",(pace5k%60)) + "/km";
                break;
            case "10k":
                int pace10k = (int) Math.round(time/10);
                pace = (pace10k/60) + ":" + String.format("%02d",(pace10k%60)) + "/km";
                break;
            case "half_marathon":
                int paceHalf = (int) Math.round(time/21.0975);
                pace = (paceHalf/60) + ":" + String.format("%02d",(paceHalf%60)) + "/km";
                break;
            case "marathon":
                int paceFull = (int) Math.round(time/42.195);
                pace = (paceFull/60) + ":" + String.format("%02d",(paceFull%60)) + "/km";
                break;
        }

        textView5.setText("Percentile of " + gender.toLowerCase() + " runners:");

        percentileTextView.setText(Integer.toString(percentile) + "%");
        allPercentileTextView.setText(Integer.toString(allPercentile) + "%");
        ageGradeTextView.setText(Integer.toString(ageGrade) + "%");
        paceTextView.setText(pace);
    }

    private int getPercentile(String sql){
        int time = getTimeInSeconds();
        MyDataBase runningDatabase = new MyDataBase(this);
        SQLiteDatabase runningSQLDatabase = runningDatabase.getReadableDatabase();

        Cursor cursor = runningSQLDatabase.rawQuery(sql,null);
        HashMap<Integer,Integer> hashMap = new HashMap<>();

        String timeColumn = distance + "_time";
        while(cursor.moveToNext()){
            int percentile = cursor.getInt(cursor.getColumnIndexOrThrow("percentile"));
            int mTime = cursor.getInt(cursor.getColumnIndexOrThrow(timeColumn));
            hashMap.put(percentile,mTime);
        }
        int runningPercentile = 1;
        int runningTimeDifference = 99999999;
        for(Map.Entry<Integer,Integer> entry: hashMap.entrySet()){
            int difference = Math.abs(time - entry.getValue());
            if(difference < runningTimeDifference){
                runningTimeDifference = difference;
                runningPercentile = entry.getKey();
            }
        }
        cursor.close();
        return 100-runningPercentile;
    }

    private int getAgeGrade(int age){
        MyDataBase runningDatabase = new MyDataBase(this);
        SQLiteDatabase runningSQLDatabase = runningDatabase.getReadableDatabase();

        String timeColumn = distance + "_time";
        String sql = "SELECT * FROM " + gender + "_age_grading" + " WHERE age=" + age;
        Cursor cursor = runningSQLDatabase.rawQuery(sql,null);
        double fastestTime = 0;

        while(cursor.moveToNext()){
            fastestTime = cursor.getDouble(cursor.getColumnIndexOrThrow(timeColumn));
        }
        double runnerTime = (double) getTimeInSeconds();
        int ageGrade = (int) Math.round((fastestTime/runnerTime)*100);

        cursor.close();
        return ageGrade;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
