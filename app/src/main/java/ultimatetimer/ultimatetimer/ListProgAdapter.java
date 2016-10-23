package ultimatetimer.ultimatetimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ultimatetimer.ultimatetimer.TimerActivityPackage.TimerActivity;

/**
 * Created by pierre on 08/05/2016.
 */
public class ListProgAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    Context context;
    ArrayList<Timer> myList = new ArrayList<Timer>();

    public ListProgAdapter(Context context, ArrayList<Timer> aList) {
        this.context = context;
        this.myList = aList;
    }

    public int getCount() {
        return myList.size();
    }

    public Timer getItem(int position) {
        return myList.get(position);
    }

    //retourne l'id en fonction de la position spécifiée
    public long getItemId(int position) {
        return myList.indexOf(getItem(position));
    }

    //Retourne la vue d'un element de la liste
    public View getView(int position, View convertView, ViewGroup parent) {
        IntervalView intervalView = null;

        //Au premier appel convertView est null, on inflate le layout
        if (convertView == null) {
            Log.i("ListProgAdapter","Adapter:Timer::FirstInstanciation");
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.program_item_layout, parent, false);

            intervalView = new IntervalView();
            intervalView.Name = (TextView) convertView.findViewById(R.id.tvname);
            intervalView.Description = (TextView) convertView.findViewById(R.id.tvdescription);

            convertView.setTag(intervalView);
        }
        else
        {
            Log.i("ListProgAdapter","Adapter:Timer::RecyclingView");
            intervalView = (IntervalView) convertView.getTag();
        }

        Timer timer = (Timer) getItem(position);

        Log.i("ListProgAdapter","Adapter:Timer::"+timer.getName()+"::"+timer.getDescription());
        intervalView.Description.setText(timer.getDescription());
        intervalView.Name.setText(timer.getName());

        //Si le timer sélectionné est une durée, on affiche les valeurs
        if (timer instanceof Duration) {
            Duration duration = (Duration) timer;
            intervalView.Name.setText(duration.getHours()+":"+duration.getMinutes()+":"+duration.getSeconds());
        }

        return convertView;
    }

    //La classe IntervalView permet de ne pas devoir rechercher les vues
    //à chaque appel de getView => gain de perf
    private class IntervalView {
        TextView Name, Description;
    }

    //pour le moment on affiche un toast
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //Lancement de l'activité Timer depuis la ligne sélectionnée
        Intent intent = new Intent(ListProgAdapter.this.context, TimerActivity.class);
        intent.putExtra("program", ListProgAdapter.this.getItem(position));
        context.startActivity(intent);
    }
}
