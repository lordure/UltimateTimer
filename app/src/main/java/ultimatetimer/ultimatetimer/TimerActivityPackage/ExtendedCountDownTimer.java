package ultimatetimer.ultimatetimer.TimerActivityPackage;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;

/**
 * Created by pierre on 27/09/2016.
 */
public class ExtendedCountDownTimer extends CountDownTimer {
    private long mMillis = 0;
    CountDownListener mListener;

    public ExtendedCountDownTimer(long aMillisInFuture, long aCountDownInterval, CountDownListener aListener)
    {
        super( aMillisInFuture, aCountDownInterval);
        mListener = aListener;

    }

    public void go()
    {
        this.start();
        if( mListener != null)
            mListener.onGo();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        this.mMillis = millisUntilFinished;

        if( mListener != null)
            mListener.onTick();
    }

    @Override
    public void onFinish() {
        if( mListener != null) {
            this.mMillis = 0;
            mListener.onFinish();
        }
    }

    public long getMillisUntilFinished()
    {
        return this.mMillis;
    }

    public String getFormatedCountDown()
    {
        //On doit afficher les secondes à l'arrond supérieur
        boolean wUp = ((long) mMillis % 1000) != 0;
        long mSeconds = (long) mMillis / 1000;
        long hours = (long) mSeconds / (3600);
        long minutes = (long) (mSeconds % 3600) / 60;
        long seconds = (long) (mSeconds % 3600) % 60;
        if (wUp)
            seconds ++;
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
