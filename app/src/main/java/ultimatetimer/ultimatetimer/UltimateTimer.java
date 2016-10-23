package ultimatetimer.ultimatetimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    public final static String SP_APP_NAME = "UltimateTimer";

    private ListProgAdapter mAdapter = new ListProgAdapter(this, mUltimateList);
    private ListView listPrograms;
    private FloatingActionButton addProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ultimate_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listPrograms = (ListView) this.findViewById(R.id.listPrograms);
        addProg = (FloatingActionButton) this.findViewById(R.id.fb_add_prog);

        //On récupère la liste des programs enregistrés
        getUltimateList();

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
    protected void onResume() {
        super.onResume();
        this.loadSharedPrefs("tabata");

        this.mAdapter.notifyDataSetChanged();
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
        //Si la liste n'est pas vide alors on la clear
        if(!mUltimateList.isEmpty())
        {
            mUltimateList.clear();
            mUltimateNames.clear();
        }

        //Le nom des programmes est stocké dans un SharedPreferences "ultimatetimer"
        SharedPreferences wUltimateSharedPref = getSharedPreferences(SP_FILE_FORMAT + SP_APP_NAME, Context.MODE_APPEND);
        int wSize = wUltimateSharedPref.getInt(CreateProgram.SP_SIZE, 0);

        for(int i = 0; i < wSize; i ++)
        {
            mUltimateNames.add(wUltimateSharedPref.getString(CreateProgram.SP_PREFIX+i,null));
            //Et on ouvre le SP correspondant
            SharedPreferences wProgramSP = getSharedPreferences(mUltimateNames.get(i),Context.MODE_APPEND);
            Timer wTimer = Program.GetProgramFromSharedPreferences(wProgramSP,this);
            if( wTimer != null)
                mUltimateList.add(wTimer);
        }
        Log.i("UltimateTimerActivity", "UltimateList done");
        mAdapter.notifyDataSetChanged();
    }

    public void UpdateTrainingList() {
        this.mAdapter.notifyDataSetChanged();
        mUltimateNames.add(mUltimateList.get(mUltimateList.size()-1).getName());
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

