package ultimatetimer.ultimatetimer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pierre on 08/05/2016.
 */
//TODO Ici implémenter l'interfae Parcelable permet de transmettre l'objet entre activity
public abstract class Timer implements Parcelable {
    String mName;
    String mDescription;

    public Timer()
    {
        this.mName = "name";
        this.mDescription = "";
    }

    public Timer(String aName, String aDescription)
    {
        this.mName = aName;
        this.mDescription = aDescription;
    }

    public void setName(String aName) {
        this.mName = aName;
    }

    public void setDescription(String aDescription) {
        this.mDescription = aDescription;
    }

    public String getName() {
        return this.mName;
    }

    public String getDescription() {
        return this.mDescription;
    }

    //Implémentation du Parcelable
    @Override
    public int describeContents()
    {
        //On renvoie 0 car notre classe ne contient pas de FileDescriptor
        return 0;
    }

}

