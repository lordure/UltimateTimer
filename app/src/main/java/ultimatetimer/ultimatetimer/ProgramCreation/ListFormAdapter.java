package ultimatetimer.ultimatetimer.ProgramCreation;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;

import ultimatetimer.ultimatetimer.Duration;
import ultimatetimer.ultimatetimer.Program;
import ultimatetimer.ultimatetimer.R;
import ultimatetimer.ultimatetimer.Timer;

/**
 * Created by pierre on 10/08/2016.
 * Adapter dédié à l'affichage des programs et de durations
 * pour la création de nouveaux programmes
 */
public class ListFormAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Timer> mList = new ArrayList<>();
    LayoutInflater mInflater;

    public ListFormAdapter(Context aContext) {
        mContext = aContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ArrayList<Timer> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Timer getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Timer aTimer) {
        mList.add(aTimer);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (mList.get(position) instanceof Duration) {
            DurationView myView = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.interval_form_item_layout, parent, false);
                myView = new DurationView();
                myView.name = (TextView) convertView.findViewById(R.id.interval_name);
                myView.description = (TextView) convertView.findViewById(R.id.interval_description);
                myView.duration = (TextView) convertView.findViewById(R.id.interval_duration);

                Log.i("ListFormAdapter", "Ajout d'une duration : name::" + mList.get(position).getName());
                myView.name.setText(mList.get(position).getName());
                myView.description.setText(mList.get(position).getDescription());
                myView.duration.setText(((Duration) mList.get(position)).getHours() + ":" + ((Duration) mList.get(position)).getMinutes() + ":" + ((Duration) mList.get(position)).getSeconds());

                convertView.setTag(myView);
            } else {
                myView = (DurationView) convertView.getTag();
            }
        } else if (mList.get(position) instanceof Program) {
            ProgramView myView = null;
            Log.i("ListFormAdapter", "Program to add received");
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.program_form_item_layout, parent, false);
                myView = new ProgramView();
                myView.program = (TextView) convertView.findViewById(R.id.program_expandablelist);
                myView.program.setText(mList.get(position).getName());

                convertView.setTag(myView);
            } else {
                myView = (ProgramView) convertView.getTag();
            }
        }

        return convertView;
    }

    private class ProgramView {
        TextView program;
    }

    private class DurationView {
        TextView name;
        TextView description;
        TextView duration;
    }
}
