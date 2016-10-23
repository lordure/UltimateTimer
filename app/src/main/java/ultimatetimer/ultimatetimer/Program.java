/**
 * Created by pierre on 08/05/2016.
 */
package ultimatetimer.ultimatetimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class Program extends Timer implements Parcelable
{
    private final static String SP_Name = "Name";
    private final static String SP_Description = "Description";
    private final static String SP_Timer_Pre = "Timer_";
    private final static String SP_Is_Program = "_Is_Program";
    private final static String SP_Duration_Hours = "_Hours";
    private final static String SP_Duration_Minutes = "_Minutes";
    private final static String SP_Duration_Seconds = "_Seconds";
    private final static String SP_SUFFIXE_Name = "_Name";
    private final static String SP_SUFFIXE_Description = "_Description";
    private final static String SP_Size = "Size";

    ArrayList<Timer> listTimer;


    public Program(String Name, String Description, ArrayList<Timer> listTimer) {
        this.mName = Name;
        this.mDescription = Description;
        this.listTimer = listTimer;
    }

    public ArrayList<Timer> getListTimer() {
        return listTimer;
    }

    public void setListTimer(ArrayList<Timer> listTimer) {
        listTimer = listTimer;
    }

    public void setProgramList(Timer timer)
    {
        listTimer.add(timer);
    }

    public void setProgramList(Timer timer, int location)
    {
        listTimer.add(location, timer);
    }

    public Timer getProgramList(int location)
    {
        return listTimer.get(location);
    }

    public void addProgramInList(Timer timer) { listTimer.add(timer);}


    //Implémentation du Parcelable
    //Implémentation de la fonction statique
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeList(listTimer);
    }

    public static final Parcelable.Creator<Timer> CREATOR = new Parcelable.Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel source) {
            return new Program(source);
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    public Program(Parcel in) {
        this.mName = in.readString();
        this.mDescription = in.readString();
        listTimer = new ArrayList<Timer>();
        in.readList(listTimer, getClass().getClassLoader());
    }

    //Méthode de sauvegarde d'un program sur le téléphone
    public void SaveAsSharedPreferences(SharedPreferences sharedPref)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        //On réécrit tout, donc si le program existait avant on le reset
        editor.clear();

        //Maintenant on écrit les bonnes valeurs
        editor.putString(SP_Name,mName);
        editor.putString(SP_Description, mDescription);
        editor.putInt(SP_Size, listTimer.size());
        for(int i = 0; i < listTimer.size(); i ++)
        {
            if( listTimer.get(i) instanceof Program)
            {
                editor.putBoolean(SP_Timer_Pre + String.valueOf(i) + SP_Is_Program, Boolean.TRUE);
                editor.putString(SP_Timer_Pre+String.valueOf(i)+SP_SUFFIXE_Name, listTimer.get(i).getName());
            }
            else if(listTimer.get(i) instanceof Duration )
            {
                editor.putBoolean(SP_Timer_Pre + String.valueOf(i) + SP_Is_Program, Boolean.FALSE);
                editor.putString(SP_Timer_Pre+String.valueOf(i)+SP_SUFFIXE_Name, listTimer.get(i).getName());
                editor.putString(SP_Timer_Pre+String.valueOf(i)+SP_SUFFIXE_Description, listTimer.get(i).getDescription());
                editor.putInt(SP_Timer_Pre + String.valueOf(i) + SP_Duration_Hours, ((Duration) listTimer.get(i)).getHours());
                editor.putInt(SP_Timer_Pre + String.valueOf(i) + SP_Duration_Minutes, ((Duration) listTimer.get(i)).getMinutes());
                editor.putInt(SP_Timer_Pre + String.valueOf(i) + SP_Duration_Seconds, ((Duration) listTimer.get(i)).getSeconds());
            }
        }

        editor.commit();
    }

    //Methode de récupération d'un programme sauvegardé sur le téléphone
    //Cette méthode est static car c'est elle qui va instancier un Program
    public static Program GetProgramFromSharedPreferences(SharedPreferences sharedPref, Context c)
    {
        //Ici on va commencer par instancier un Program que l'on va ensuite construire au fur et à mesure
        String name = new String();
        String description = new String();
        int size = 0;

        name = sharedPref.getString(SP_Name,null);
        description = sharedPref.getString(SP_Description,null);
        size = sharedPref.getInt(SP_Size, 0);

        if( name == null || size == 0)
        {
            return null;
        }

        Program prog = new Program(name, description, new ArrayList<Timer>());

        //Et maintenant le plus dur
        //On parcours le reste des arguments pour récupérer le program complet

        for(int i = 0; i < size; i ++)
        {
            //On commence par le type du Timer en cours de récupération
            Boolean Is_Program = false;
            sharedPref.getBoolean(SP_Timer_Pre+String.valueOf(i)+ SP_Is_Program, Is_Program);

            if(Is_Program)
            {
                //On récupère le nom et on appelle notre fonction de récupération par récursion
                String Sub_Name = new String();
                sharedPref.getString(SP_Timer_Pre + String.valueOf(i) + SP_SUFFIXE_Name, Sub_Name);
                String Sub_Description = new String();
                sharedPref.getString(SP_Timer_Pre + String.valueOf(i) + SP_SUFFIXE_Description, Sub_Description);

                //On sait que les programs sont stockés sous un fichier dont le nom est formé à partir du nom
                SharedPreferences subSharedPred = c.getSharedPreferences(UltimateTimer.SP_FILE_FORMAT+Sub_Name,Context.MODE_PRIVATE);
                //On récupère le program associé
                if (subSharedPred == null)
                {
                    return null;
                }

                Program subProgram = Program.GetProgramFromSharedPreferences(subSharedPred,c);

                prog.addProgramInList(subProgram);
            }
            else
            {
                //Sinon on crée une duration que l'on ajoute à la listTimer
                String Sub_Name = new String();
                sharedPref.getString(SP_Timer_Pre + String.valueOf(i) + SP_SUFFIXE_Name, Sub_Name);
                String Sub_Description = new String();
                sharedPref.getString(SP_Timer_Pre + String.valueOf(i) + SP_SUFFIXE_Description, Sub_Description);
                int hours = sharedPref.getInt(SP_Timer_Pre + String.valueOf(i) + SP_Duration_Hours, 0);
                int minutes = sharedPref.getInt(SP_Timer_Pre + String.valueOf(i) + SP_Duration_Minutes, 0);
                int seconds = sharedPref.getInt(SP_Timer_Pre + String.valueOf(i) + SP_Duration_Seconds, 0);
                Duration duration = new Duration(Sub_Name,Sub_Description,hours,minutes,seconds);

                prog.addProgramInList(duration);
            }
        }

        return prog;
    }
}
