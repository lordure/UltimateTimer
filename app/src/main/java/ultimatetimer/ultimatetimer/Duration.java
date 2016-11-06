package ultimatetimer.ultimatetimer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pierre on 08/05/2016.
 */
public class Duration extends Timer implements Parcelable{
    int mHours;
    int mMinutes;
    int mSeconds;

    public Duration(String aName, String aDescription, int aHours, int aMinutes, int aSeconds) {
        super(aName, aDescription);
        mHours = aHours;
        mMinutes = aMinutes;
        mSeconds = aSeconds;
    }

    public int getHours() {
        return mHours;
    }

    public void setHours(int hours) {
        mHours = hours;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public void setMinutes(int minutes) {
        mMinutes = minutes;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void setSeconds(int seconds) {
        mSeconds = seconds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mHours);
        dest.writeInt(this.mMinutes);
        dest.writeInt(this.mSeconds);
        dest.writeString(this.mName);
        dest.writeString(this.mDescription);
    }

    public static final Parcelable.Creator<Duration> CREATOR = new Parcelable.Creator<Duration>()
    {
        @Override
        public Duration createFromParcel(Parcel in)
        {
            return new Duration(in);
        }

        @Override
        public Duration[] newArray(int size)
        {
            return new Duration[size];
        }
    };

    public Duration(Parcel in)
    {
        this.mHours = in.readInt();
        this.mMinutes = in.readInt();
        this.mSeconds = in.readInt();
        this.mName = in.readString();
        this.mDescription = in.readString();
    }
}
