package ultimatetimer.ultimatetimer.TimerActivityPackage;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ultimatetimer.ultimatetimer.Duration;
import ultimatetimer.ultimatetimer.Program;
import ultimatetimer.ultimatetimer.R;

public class TimerActivity extends AppCompatActivity implements CountDownListener {
    private ArrayList<Duration> mCompleteTraining = new ArrayList<Duration>();
    private long mStoredMillisInFuture = 0;
    private int mTrainingCursor = 0;
    private ExtendedCountDownTimer mCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        Button wLauncher = (Button) this.findViewById(R.id.launcher);
        Button wPauser = (Button) this.findViewById(R.id.pauser);
        Button wStopper = (Button) this.findViewById(R.id.stopper);

        //Ecriture des caractéristiques du timer en cours
        TextView tv_description = (TextView) this.findViewById(R.id.id_description);
        ListView listProgram = (ListView) this.findViewById(R.id.id_interval);

        //Setup du text size du countdownclock à la taille height du text view
        TextView tv_countdown = (TextView) this.findViewById(R.id.countdownClock);
        //float sourceTextSize = tv_countdown.getTextSize();
        //tv_countdown.setTextSize(sourceTextSize / getResources().getDisplayMetrics().density);

        //Récupération du timer demandé
        Program program = this.getIntent().getExtras().getParcelable("program");
        tv_description.setText(program.getDescription());

        toolbar.setTitle(program.getName());
        setSupportActionBar(toolbar);

        getCompleteTraining(program);

        ListDurationAdapter listDurationAdapter = new ListDurationAdapter(this, mCompleteTraining);
        listProgram.setAdapter(listDurationAdapter);


        wLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si mStoredMillisInFuture != 0 alors on est en pause
                //Sinon on lance un timer
                setCountDownTimer();
            }
        });

        //Configuration des boutons pause et stop
        //Pause
        wPauser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerActivity.this.mStoredMillisInFuture == 0) {
                    final TextView countdownClock = (TextView) TimerActivity.this.findViewById(R.id.countdownClock);
                    //En cas de pause, on stock les millis qui reste avant la fin
                    //Les millis sont déjà sauvées dans mStoredMillisInFuture
                    TimerActivity.this.mStoredMillisInFuture = TimerActivity.this.mCountDown.getMillisUntilFinished();

                    countdownClock.setTextColor(Color.YELLOW);
                    countdownClock.setText(TimerActivity.this.mCountDown.getFormatedCountDown());
                    TimerActivity.this.mCountDown.cancel();

                    Toast.makeText(TimerActivity.this, R.string.trainingpaused, Toast.LENGTH_LONG).show();
                }
            }
        });

        wStopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerActivity.this.mCountDown != null) {
                    final TextView countdownClock = (TextView) TimerActivity.this.findViewById(R.id.countdownClock);

                    TimerActivity.this.mCountDown.cancel();
                    countdownClock.setText(R.string.trainingnone);

                    Toast.makeText(TimerActivity.this, R.string.trainingcancelled, Toast.LENGTH_LONG).show();

                    TimerActivity.this.mTrainingCursor = 0;
                    TimerActivity.this.mStoredMillisInFuture = 0;
                }
            }
        });

    }

    private void setCountDownTimer() {
        //On affiche le décompte de la première Duration

        Duration duration = mCompleteTraining.get(this.mTrainingCursor);
        long millisInFuture = 0;


        //A chaque transition il y a une seconde en plus -> si ce n'est pas le premier tour on enlève un seconde :
        // TODO Vérifier cette assertion
        if (this.mStoredMillisInFuture != 0) {
            millisInFuture = this.mStoredMillisInFuture;
            this.mStoredMillisInFuture = 0;
        } else {
            millisInFuture = duration.getHours() * (60 * 60 * 1000) + duration.getMinutes() * 60 * 1000 + duration.getSeconds() * 1000;
        }

        //Une fois que l'on a affiché le programme en entier on affiche le chrono
        //Classe : CountdownTimer
        this.mCountDown = new ExtendedCountDownTimer(millisInFuture, 100, this);

        this.mCountDown.start();
    }

    //Pour afficher tout les intevalles voulu on va parcourir l'intégralité des Durations et programs
    //afin d'avoir tout les timers au même niveau dans une liste simple
    private void getCompleteTraining(Program program) {
        //On récupère la liste de notre program

        for (int i = 0; i < program.getListTimer().size(); i++) {
            if (program.getListTimer().get(i) instanceof Duration) {
                mCompleteTraining.add((Duration) program.getListTimer().get(i));
            }
            //Sinon il faut parcourir les différents programmes imbriqués jusqu'à trouver une Duration
            else if (program.getListTimer().get(i) instanceof Program) {
                getCompleteTraining((Program) program.getListTimer().get(i));
            }
        }
    }

    //Implémentation de l'interface pour le listener du countdown fait maison
    public void onGo() {

    }

    public void onTick() {
        final TextView countdownClock = (TextView) this.findViewById(R.id.countdownClock);
        countdownClock.setTextColor(Color.BLACK);
        countdownClock.setText(this.mCountDown.getFormatedCountDown());
    }

    public void onFinish() {
        //2 cas possible. Soit il y a un timer encore après soit c'est fini.
        //Dans tous les cas on bips ^^

        final TextView countdownClock = (TextView) this.findViewById(R.id.countdownClock);

        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

        //On set le compteur à 0
        countdownClock.setTextColor(Color.BLACK);
        countdownClock.setText(R.string.trainingnone);

        if (TimerActivity.this.mTrainingCursor == mCompleteTraining.size() - 1) {
            //Alors c'est fini bien fini
            //On réinitialise mTrainingCursor sans appeler setCountDownTimer pour ne pas relancer l'appli
            TimerActivity.this.mTrainingCursor = 0;
        } else {
            //Sinon on relance la fonction avec la duration suivante
            //setCountDownTimer(i + 1);
            TimerActivity.this.mTrainingCursor++;
            //Et on appelle setCOuntDownTimer ue nouvelle fois
            this.setCountDownTimer();
        }
    }
}
