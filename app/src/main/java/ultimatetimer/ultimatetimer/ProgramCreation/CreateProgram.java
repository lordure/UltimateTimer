package ultimatetimer.ultimatetimer.ProgramCreation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;

import ultimatetimer.ultimatetimer.Duration;
import ultimatetimer.ultimatetimer.Program;
import ultimatetimer.ultimatetimer.R;
import ultimatetimer.ultimatetimer.Timer;
import ultimatetimer.ultimatetimer.UltimateTimer;

/**
 * Activité de création d'un program
 * Permet de choisir le nom et la description du nouveau program
 * On peut y ajouter d'autre programmes ou juste un interval.
 *
 */
public class CreateProgram extends AppCompatActivity {
    static final ArrayList<Timer> mListTraining = new ArrayList<Timer>();
    ListFormAdapter mAdapter;

    public static final int DURATION_SELECTION = 666;
    public static final int PROGRAM_SELECTION = 777;

    @Override
    protected void onCreate(Bundle aSavedInstance) {
        super.onCreate(aSavedInstance);
        this.setContentView(R.layout.content_form);

        Toolbar wToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(wToolbar);

        ListView wListView = (ListView) this.findViewById(R.id.list_training);

        mAdapter = new ListFormAdapter(this);
        wListView.setAdapter(mAdapter);
    }

    //On rajoute l'option done dans la toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                String wName = ((EditText) this.findViewById(R.id.text_name)).getText().toString();
                String wDescription = ((EditText) this.findViewById(R.id.text_description)).getText().toString();

                ArrayList<Timer> wListTraining = ((ListFormAdapter) ((ListView) this.findViewById(R.id.list_training)).getAdapter()).getList();

                Program wNewProg = new Program(wName, wDescription, wListTraining);

                //On sauvegarde notre programme dans la mémoire du téléphone
                SharedPreferences wSP = getSharedPreferences(wNewProg.getName(), Context.MODE_PRIVATE);
                wNewProg.SaveAsSharedPreferences(wSP);

                UltimateTimer.mUltimateList.add(wNewProg);

                //Mise à jour du fichier qui contient la liste de tout les programmes
                SharedPreferences wUltimateTimer = getSharedPreferences("UltimateTimer", Context.MODE_APPEND);
                int wSize = wUltimateTimer.getInt("Size", 0);
                SharedPreferences.Editor wEditor = wUltimateTimer.edit();
                wEditor.putString("Program_"+wSize, wNewProg.getName());
                wSize ++;
                wEditor.putInt("Size",wSize);
                wEditor.commit();

                this.finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDurationItem(View v) {
        //Création et affichage du dialog de sélection de duration
        AlertDialog.Builder wBuilder = new AlertDialog.Builder(this);

        LayoutInflater wLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View wView = wLayoutInflater.inflate(R.layout.choose_duration_dialog_layout, null);
        wBuilder.setView(wView);
        final NumberPicker wHours = (NumberPicker) wView.findViewById(R.id.number_hours);
        final NumberPicker wMinutes = (NumberPicker) wView.findViewById(R.id.number_minutes);
        final NumberPicker wSeconds = (NumberPicker) wView.findViewById(R.id.number_seconds);
        final EditText wName = (EditText) wView.findViewById(R.id.choose_name_duration);
        final EditText wDescription = (EditText) wView.findViewById(R.id.choose_description_duration);

        wHours.setMinValue(0);
        wHours.setMaxValue(23);
        wHours.setWrapSelectorWheel(true);
        wHours.computeScroll();

        wMinutes.setMinValue(0);
        wMinutes.setMaxValue(59);
        wMinutes.setWrapSelectorWheel(true);

        wSeconds.setMinValue(0);
        wSeconds.setMaxValue(59);
        wSeconds.setWrapSelectorWheel(true);

        wBuilder.setMessage("Choose your duration set up");
        wBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //On affiche le button set positive uniquement si name n'est pas vide et s'il y a au moins un Timer ajouté
        wBuilder.setPositiveButton("Add",null);

        final AlertDialog wDialog = wBuilder.create();

        wDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface dialog) {
                                          Button b = wDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                          b.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  if( wName.getText() != null && !(wHours.getValue() == 0 && wMinutes.getValue() == 0 && wSeconds.getValue() == 0))
                                                  {
                                                      mAdapter.addItem(new Duration(wName.getText().toString(),
                                                              wDescription.getText().toString(),
                                                              wHours.getValue(),
                                                              wMinutes.getValue(),
                                                              wSeconds.getValue()));

                                                      Toast.makeText(CreateProgram.this, "Duration " + wName.getText().toString() + " added.", Toast.LENGTH_LONG).show();
                                                  }
                                                  wDialog.dismiss();
                                              }
                                          });

                                      }
                                  });
        wDialog.show();

    }

    public void selectProgramItem(View v) {
        //Notre builder pour le dialog
        AlertDialog.Builder wBuilder = new AlertDialog.Builder(this);

        LayoutInflater wInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View wView = wInflater.inflate(R.layout.choose_program_dialog_layout, null);
        wBuilder.setView(wView);

        final ListView wListView = (ListView) wView.findViewById(R.id.program_list);
        //on affiche uniquement le nom des programmes disponibles
        //final ArrayList<String> wDisplayedList = new ArrayList<>();
        //for (int i = 0; i < UltimateTimer.mUltimateList.size(); i++) {
        //    wDisplayedList.add(UltimateTimer.mUltimateList.get(i).getName());
        //}
        wListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, UltimateTimer.mUltimateNames));
        wListView.setOnItemClickListener(new OnProgramClickListener());

        wBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        wBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (((OnProgramClickListener) wListView.getOnItemClickListener()).hasSelected()) {
                    int wPosition = ((OnProgramClickListener) wListView.getOnItemClickListener()).getSelectedItem();
                    mAdapter.addItem((Program) UltimateTimer.mUltimateList.get(wPosition));
                    mAdapter.notifyDataSetChanged();

                    Toast.makeText(CreateProgram.this, "Program " + "" + " added.", Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog wDialog = wBuilder.create();
        wDialog.show();
    }

    public Timer getListTrainingItem(int position) {
        return mAdapter.getItem(position);
    }

    private class OnProgramClickListener implements AdapterView.OnItemClickListener {
        private int mSelectedItem = -1;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedItem = position;
        }

        public boolean hasSelected() {
            return mSelectedItem != -1;
        }

        public int getSelectedItem() {
            return mSelectedItem;
        }
    }

}
