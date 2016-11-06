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
        this.setTitle(getString(R.string.khronos));

        this.checkFirstRun();

        this.checkFirstRun();

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
        this.loadSharedPrefs("Tabata");
        this.loadSharedPrefs("EMOM");

        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_ultimate_timer, menu);
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

    private void checkFirstRun() {

        final String SP_CONFIGURATION = "Khronos_configuration";
        final String SP_VERSION_CODE = "version_code";
        final int DOESNT_EXIST = -1;


        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(SP_CONFIGURATION, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(SP_VERSION_CODE, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode)
        {
            // This is just a normal run
            return;
        }
        else if (savedVersionCode == DOESNT_EXIST)
        {
            //Dans le cadre d'une nouvelle install on pre-enregistre certains programmes, les classqiques ^^
            Program wTabata = getTabata();
            UltimateTimer.mUltimateNames.add("Tabata");
            UltimateTimer.mUltimateList.add(wTabata);
            SharedPreferences wSP = getSharedPreferences(wTabata.getName(), Context.MODE_PRIVATE);
            wTabata.SaveAsSharedPreferences(wSP);

            Program wEMOM = getEMOM();
            UltimateTimer.mUltimateNames.add("EMOM");
            UltimateTimer.mUltimateList.add(wEMOM);
            SharedPreferences wSP2 = getSharedPreferences(wEMOM.getName(), Context.MODE_PRIVATE);
            wEMOM.SaveAsSharedPreferences(wSP2);

            //Création du fichier contenant la liste des programmes
            //Mise à jour du fichier qui contient la liste de tout les programmes
            SharedPreferences wUltimateTimer = getSharedPreferences(UltimateTimer.SP_FILE_FORMAT + UltimateTimer.SP_APP_NAME,
                    Context.MODE_APPEND);
            SharedPreferences.Editor wEditor = wUltimateTimer.edit();
            wEditor.putString(CreateProgram.SP_PREFIX + 0, wTabata.getName());
            wEditor.putString(CreateProgram.SP_PREFIX + 1, wEMOM.getName());
            wEditor.putInt(CreateProgram.SP_SIZE, 2);
            wEditor.commit();
        } else if (currentVersionCode > savedVersionCode) {
            //Rien à faire pour le moment
            ;
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(SP_VERSION_CODE, currentVersionCode).commit();

    }

    private final Program getTabata()
    {
        //TABATA
        Duration w20 = new Duration(getString(R.string.exercice),getString(R.string.whatever),0,0,20);
        Duration w10 = new Duration(getString(R.string.rest),null, 0,0,10);
        ArrayList<Timer> wTabataList = new ArrayList<Timer>();
        wTabataList.add(new Duration(getString(R.string.preparation),getString(R.string.prepareforworkout), 0,0,15));
        wTabataList.add(w20);
        wTabataList.add(w10);
        wTabataList.add(w20);
        wTabataList.add(w10);
        wTabataList.add(w20);
        wTabataList.add(w10);
        wTabataList.add(w20);
        wTabataList.add(w10);
        Program wTabata = new Program("Tabata","HIIT", wTabataList);

        return wTabata;
    }

    private final Program BeteVosges()
    {
        return null;
    }

    private final Program getEMOM()
    {
        ArrayList<Timer> wList = new ArrayList<>();
        wList.add(new Duration(getString(R.string.preparation),null, 0,0,15));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        wList.add(new Duration(getString(R.string.exercice),null, 0,1,0));
        Program wEMOM = new Program("EMOM",getString(R.string.emom),wList );

        return wEMOM;
    }

}

