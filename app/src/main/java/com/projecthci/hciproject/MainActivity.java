package com.projecthci.hciproject;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    private Schedule testSchedule;
    private int testStage = 0; // 0=reminder, 1=graph
    private Calendar lastNotification;
    private ArrayList<Integer> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lastNotification = null;
        scores = new ArrayList<>();
        scores.add(0);

        //LOAD SCORES FROM FILE
        try {
            scores = readScoresFromFile();
            Log.d("I/O message", "File CAN be read");
        } catch (Exception e) {
            Log.d("I/O message", "File cannot be read");
        }

        //Initialize the schedule
        testSchedule = new Schedule();
        testSchedule.addWorkout(new GregorianCalendar(2016,0,27,15,10,0), new Workout("Push-ups", 10));
        testSchedule.addWorkout(new GregorianCalendar(2016,0,28,16,0,0), new Workout("Push-ups", 20));
        testSchedule.addWorkout(new GregorianCalendar(2016,0,29,16,0,0), new Workout("Push-ups", 30));

        //Start background thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");

                    Calendar currentDate = new GregorianCalendar(timeZone);
                    Date currentTime = new Date();
                    currentDate.setTime(currentTime);
                    for (Map.Entry e : testSchedule.getScheduledWorkouts().entrySet()) {
                        if (datesAreEqual((Calendar) e.getKey(), currentDate) && timeGreaterThan(currentDate, (Calendar) e.getKey())) {
                            //You should do a workout, send reminder, or do something else
                            if (testStage == 0) {
                                boolean datesEqual = false;
                                if (lastNotification != null) {
                                    if (datesAreEqual(lastNotification, currentDate)) {
                                        datesEqual = true;
                                    }
                                }
                                if (!datesEqual) {
                                    sendReminder();
                                    lastNotification = currentDate;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public ArrayList<Integer> readScoresFromFile() throws Exception
    {
        FileInputStream fis;
        ArrayList<Integer> returnlist= null;
        fis = openFileInput("scores");
        ObjectInputStream ois = new ObjectInputStream(fis);
        returnlist = (ArrayList<Integer>) ois.readObject();
        ois.close();

        return returnlist;
    }

    public void writeScoresFromFile(ArrayList<Integer> scores)
    {
        FileOutputStream fis;
        ArrayList<Integer> returnlist= null;
        try {
            fis = openFileOutput("scores", Context.MODE_PRIVATE);
            ObjectOutputStream ois = new ObjectOutputStream(fis);
            ois.writeObject(scores);
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoWorkoutMenu(View view)
    {
        setContentView(R.layout.workout_menu);
    }

    public void sendReminder()
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.graph)
                        .setContentTitle("Get ready to exercise!")
                        .setContentText("Are you going to do your workout? \n'Yes, of course!'");
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void mainMenuGraphsButtonClick(View view)
    {
        setContentView(R.layout.graph_menu);
        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setScaleEnabled(false);
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0; i<scores.size(); i++)
        {
            entries.add(new Entry(scores.get(i), i));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Progress");
        dataSet.setDrawCubic(true);
        dataSet.setColor(Color.GREEN);
        ArrayList<String> xNames = new ArrayList<>();
        for(int i=0; i<scores.size(); i++)
        {
            xNames.add("Day " + (i+1));
        }
        LineData data = new LineData(xNames, dataSet);
        chart.setData(data);
        chart.setDescription("");
        chart.setExtraTopOffset(10);
        chart.invalidate();
    }

    public void mainMenuFriendsButtonClick(View view)
    {
        setContentView(R.layout.friends_menu);
    }

    public void mainMenuWorkoutsButtonClick(View view)
    {
        setContentView(R.layout.workout_menu);
    }

    public void startWorkoutButtonClick(View view)
    {
        setContentView(R.layout.start_workout);

        TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");

        Calendar currentDate = new GregorianCalendar(timeZone);
        Date currentTime = new Date();
        currentDate.setTime(currentTime);

        int hours = (currentDate.getTime().getHours()+1<24?currentDate.getTime().getHours()+1:0);
        int minutes = currentDate.getTime().getMinutes();

        Workout todo = null;

        for(Map.Entry e : testSchedule.getScheduledWorkouts().entrySet())
        {
            if(datesAreEqual((Calendar)e.getKey(), currentDate))
            {
                todo = (Workout) e.getValue();
                break;
            }
        }

        TextView t = (TextView)findViewById(R.id.workoutDesc);
        t.setText(todo.getRepititions() + "x " + todo.getName());
    }

    public boolean datesAreEqual(Calendar one, Calendar two) {
        return (one.get(Calendar.YEAR) == two.get(Calendar.YEAR) && one.get(Calendar.DAY_OF_MONTH) == two.get(Calendar.DAY_OF_MONTH) && one.get(Calendar.MONTH) == two.get(Calendar.MONTH));
    }

    public boolean timeGreaterThan(Calendar toTest, Calendar cal)
    {
        return (toTest.get(Calendar.HOUR_OF_DAY) >= cal.get(Calendar.HOUR_OF_DAY));
    }

    public void workoutResultDoneButtonClick(View view)
    {
        NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker);
        int repititions = np.getValue();
        Workout todo = null;
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Amsterdam");

        Calendar currentDate = new GregorianCalendar(timeZone);
        Date currentTime = new Date();
        currentDate.setTime(currentTime);

        for(Map.Entry e : testSchedule.getScheduledWorkouts().entrySet())
        {
            if(datesAreEqual((Calendar)e.getKey(), currentDate))
            {
                todo = (Workout) e.getValue();
                break;
            }
        }

        TextView t = (TextView)findViewById(R.id.workoutDesc);

        int scoreEarned = 10-(Math.abs(todo.getRepititions() - repititions));
        scores.add(scores.get(scores.size()-1) + scoreEarned);
        writeScoresFromFile(scores);
        setContentView(R.layout.activity_main);
    }

    public void scheduleButtonClick(View view)
    {
        String scheduleString = "";
        for(Map.Entry e : testSchedule.getScheduledWorkouts().entrySet())
        {
            Calendar date = ((Calendar)e.getKey());
            Workout workout = ((Workout)e.getValue());
            String temp = "";
            temp+=date.get(Calendar.DAY_OF_MONTH)+"-"+(date.get(Calendar.MONTH)+1) + "-" + date.get(Calendar.YEAR) + "\n";
            temp+=workout.getRepititions() + "x " + workout.getName() + "\n\n";

            scheduleString = temp + scheduleString;
        }
        setContentView(R.layout.schedule);
        TextView scheduleTextView = (TextView) findViewById(R.id.workoutScheduleTextView);
        scheduleTextView.setText(scheduleString);
    }

    public void stopWorkoutButtonClick(View view)
    {
        setContentView(R.layout.workout_result);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.oneToTen, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
