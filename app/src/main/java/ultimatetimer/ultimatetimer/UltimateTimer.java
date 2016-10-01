package ultimatetimer.ultimatetimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ultimatetimer.ultimatetimer.ProgramCreation.CreateProgram;

public class UltimateTimer extends AppCompatActivity {

    public static ArrayList<Timer> mUltimateList = new ArrayList<>();
    public static ArrayList<String> mUltimateNames = new ArrayList<>();

    public final static String SP_FILE_FORMAT = "com.ultimatetimer.app.";

    private ListProgAdapter mAdapter = new ListProgAdapter(this, mUltimateList);
    private ListView listPrograms;
    private FloatingActionButton addProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimate_timer);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        listPrograms = (ListView) this.findViewById(R.id.listPrograms);
        addProg = (FloatingActionButton) this.findViewById(R.id.fb_add_prog);

        //On récupère la liste des programs enregistrés
        getUltimateList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //On assigne l'adapter à notre list
        listPrograms.setAdapter(mAdapter);
        listPrograms.setOnItemClickListener(mAdapter);

        addProg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UltimateTimer.this, CreateProgram.class);
                UltimateTimer.this.startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ultimate_timer, menu);
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

    private void getUltimateList() {
        //TODO mise en place de la récupération des données
        //Le nom des programmes est stocké dans un SharedPreferences "ultimatetimer"
        SharedPreferences wUltimateSharedPref = getSharedPreferences("UltimateTimer", Context.MODE_APPEND);
        int wSize = wUltimateSharedPref.getInt("Size", 0);

        for(int i = 0; i < wSize; i ++)
        {
            mUltimateNames.add(wUltimateSharedPref.getString("Program_"+i,null));
            //Et on ouvre le SP correspondant
            SharedPreferences wProgramSP = getSharedPreferences(mUltimateNames.get(i),Context.MODE_APPEND);
            Timer wTimer = Program.GetProgramFromSharedPreferences(wProgramSP,this);
            if( wTimer != null)
                mUltimateList.add(wTimer);
        }

        //Pour le moment on écrit en dur nos data
        //ArrayList<Timer> list_1 = new ArrayList<Timer>();
        //list_1.add(new Duration("Duration 1", "Description 1", 0, 1, 10));
        //list_1.add(new Duration("Duration 2", "Description 2", 0, 0, 10));
        //list_1.add(new Duration("Duration 3", "Description 3", 7, 8, 9));
        //ArrayList<Timer> list_2 = new ArrayList<Timer>();
        //list_2.add(new Duration("Duration 4", "Description 4", 10, 20, 30));
        //ArrayList<Timer> list_3 = new ArrayList<Timer>();
        //list_3.add(new Duration("Duration 5", "Description 5", 100, 200, 300));
        //Program program_1 = new Program("Program 1", "Description program 1", list_1);
        //Program program_2 = new Program("Program 2", "Description program 2", list_2);
        //Program program_3 = new Program("Program 3", "Description program 3", list_3);
//
        //mUltimateList.add(program_1);
        //mUltimateList.add(program_2);
        //mUltimateList.add(program_3);

        //DEBUG
        this.loadSharedPrefs("UltimateTimer","name 1");

        Log.i("UltimateTimerActivity", "UltimateList done");
    }

    public void UpdateTrainingList() {
        this.mAdapter.notifyDataSetChanged();
    }

    public void loadSharedPrefs(String ... prefs) {

        // Define default return values. These should not display, but are needed
        final String STRING_ERROR = "error!";
        final Integer INT_ERROR = -1;
        // ...
        final Set<String> SET_ERROR = new HashSet<>(1);

        // Add an item to the set
        SET_ERROR.add("Set Error!");

        // Loop through the Shared Prefs
        Log.i("Loading Shared Prefs", "-----------------------------------");
        Log.i("------------------", "-------------------------------------");

        for (String pref_name: prefs) {

            SharedPreferences preference = getSharedPreferences(pref_name, MODE_PRIVATE);
            Map<String, ?> prefMap = preference.getAll();

            Object prefObj;
            Object prefValue = null;

            for (String key : prefMap.keySet()) {

                prefObj = prefMap.get(key);

                if (prefObj instanceof String) prefValue = preference.getString(key, STRING_ERROR);
                if (prefObj instanceof Integer) prefValue = preference.getInt(key, INT_ERROR);
                // ...
                if (prefObj instanceof Set) prefValue = preference.getStringSet(key, SET_ERROR);

                Log.i(String.format("Shared Preference : %s - %s", pref_name, key),
                        String.valueOf(prefValue));

            }

            Log.i("------------------", "-------------------------------------");

        }

        Log.i("Loaded Shared Prefs", "------------------------------------");

    }

}

