package ultimatetimer.ultimatetimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by pierre on 17/12/2016.
 */
public class ListViewLongClick implements AbsListView.MultiChoiceModeListener {

    private Context mContext;
    private ArrayList<String> mSelected = new ArrayList<>();
    private ListProgAdapter mAdapter;

    ListViewLongClick(Context c, ListProgAdapter aAdapter)
    {
        this.mContext = c;
        this.mAdapter = aAdapter;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
        if(checked)
        {
            mSelected.add(UltimateTimer.mUltimateNames.get(position));
        }
        else
        {
            mSelected.remove(UltimateTimer.mUltimateNames.get(position));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.longclick_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Here you can perform updates to the CAB due to
        // an invalidate() request
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        boolean wReturn = false;
        switch(item.getItemId())
        {
            case R.id.action_delete:
                //Delete the prog
                for(int i = 0; i < mSelected.size(); i ++)
                {
                    for (int j = 0; j < UltimateTimer.mUltimateList.size(); j ++)
                        if ( UltimateTimer.mUltimateList.get(j).getName() == mSelected.get(i)) {
                            UltimateTimer.mUltimateList.remove(j);
                            UltimateTimer.mUltimateNames.remove(mSelected.get(i));
                            //On met à jour les shared preferences
                            this.deleteProgramSP(mSelected.get(i));
                        }
                }

                this.mAdapter.notifyDataSetChanged();
                mSelected.clear();
                mode.finish();
                return true;
            case R.id.action_edit:
                //send
                mode.finish();
                wReturn  = true;
            break;
            case R.id.action_send:
                Toast.makeText(this.mContext, R.string.not_yet, Toast.LENGTH_LONG).show();
                mode.finish();
                wReturn = true;
            break;
            default:
                break;
        }

        return wReturn;

    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
    }

    public void deleteProgramSP(String name)
    {
        //First remove the name from the List of all programs:
        SharedPreferences wUltimateSharedPref = mContext.getSharedPreferences(UltimateTimer.SP_FILE_FORMAT + UltimateTimer.SP_APP_NAME, Context.MODE_APPEND);
        wUltimateSharedPref.edit().remove(name).commit();

        //then we delete the corresponing SP
        SharedPreferences wProgramSP = mContext.getSharedPreferences(name, Context.MODE_APPEND);
        wProgramSP.edit().clear().commit();
    }
}
