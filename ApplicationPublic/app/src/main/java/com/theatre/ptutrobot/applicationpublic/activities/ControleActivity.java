package com.theatre.ptutrobot.applicationpublic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.*;
import android.os.Process;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.theatre.ptutrobot.applicationpublic.R;
import com.theatre.ptutrobot.applicationpublic.connexion.Connexion;

import org.w3c.dom.Text;

import java.util.Timer;

/**
 * Created by p1203391 on 26/01/2015.
 */
public class ControleActivity extends Activity
{
    //  Boite de dialogue d'attente de permission de contrôle
    private Dialog dialog_attente = null;

    //  Boite de dialogue pour confirmer le contrôle du robot
    private Dialog dialog_selection = null;

    //  Dialog rejouer
    private AlertDialog dialog_rejouer = null;

    //  Boutons de controle du robot
    private ImageButton bouton_avancer;
    private ImageButton bouton_reculer;
    private ImageButton bouton_gauche;
    private ImageButton bouton_droite;

    //  Bouton photo
    private ImageButton bouton_photo;

    //  Seek Bar Vitesse
    private SeekBar seekbar_vitesse;

    //  Deplacement actuel
    private Direction direction;

    //  Timer
    TextView timer_controle;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle);

        //  On définit le robot comme immobile à la base
        direction = Direction.Immobile;

        //  Récupération des boutons et de la seekbar depuis la vue xml
        bouton_avancer = (ImageButton) findViewById(R.id.bouton_avancer);
        bouton_avancer.setOnTouchListener(bouton_avancer_listener);

        bouton_reculer = (ImageButton) findViewById(R.id.bouton_reculer);
        bouton_reculer.setOnTouchListener(bouton_reculer_listener);

        bouton_gauche = (ImageButton) findViewById(R.id.bouton_rotation_gauche);
        bouton_gauche.setOnTouchListener(bouton_gauche_listener);

        bouton_droite = (ImageButton) findViewById(R.id.bouton_rotation_droite);
        bouton_droite.setOnTouchListener(bouton_droite_listener);

        bouton_photo = (ImageButton) findViewById(R.id.bouton_photo);
        bouton_photo.setOnClickListener(bouton_photo_listener);

        seekbar_vitesse = (SeekBar) findViewById(R.id.seek_bar_vitesse);

        seekbar_vitesse.setMax(75);
        seekbar_vitesse.setProgress(75/2);

        Connexion.se_mettre_en_attendre(this);
    }

    //  Appelé lors de l'appui sur le bouton avancer
    View.OnTouchListener bouton_avancer_listener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Connexion.envoyer_direction(getContext(), Connexion.avancer, seekbar_vitesse.getProgress());
                direction = Direction.Avance;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                Connexion.envoyer_stop(getContext());
                direction = Direction.Immobile;
            }

            gererBoutons();
            return false;
        }
    };

    //  Appelé lors de l'appui sur le bouton reculer
    View.OnTouchListener bouton_reculer_listener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Connexion.envoyer_direction(getContext(), Connexion.reculer, seekbar_vitesse.getProgress());
                direction = Direction.Recule;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                Connexion.envoyer_stop(getContext());
                direction = Direction.Immobile;
            }

            gererBoutons();
            return false;
        }
    };

    //  Appelé lors de l'appui sur le bouton gauche
    View.OnTouchListener bouton_gauche_listener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Connexion.envoyer_direction(getContext(), Connexion.gauche, seekbar_vitesse.getProgress());
                direction = Direction.Tourne_gauche;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                Connexion.envoyer_stop(getContext());
                direction = Direction.Immobile;
            }

            gererBoutons();
            return false;
        }
    };

    //  Appelé lors de l'appui sur le bouton droite
    View.OnTouchListener bouton_droite_listener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Connexion.envoyer_direction(getContext(), Connexion.droite, seekbar_vitesse.getProgress());
                direction = Direction.Tourne_droite;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                Connexion.envoyer_stop(getContext());
                direction = Direction.Immobile;
            }

            gererBoutons();
            return false;
        }
    };

    //  Appelé lors de l'appui sur le bouton photo
    View.OnClickListener bouton_photo_listener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Connexion.prendre_photo(getContext());
        }
    };

    //  Permet d'afficher la boite de dialogue pour choisir de controler ou pas le robot
    public void afficher_dialog_accepter_controle(Long temps_attente, Long temps_controle, String couleur)
    {
        dialog_selection = new DialogSelection(this, temps_attente.intValue(), temps_controle, couleur);
        dialog_selection.setCancelable(false);
        dialog_selection.show();
    }

    //  Permet d'afficher la boite de dialogue attente de permission
    public void afficherDialogErreur()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Titre
        alertDialogBuilder.setTitle("Erreur");

        // Message d'erreur
        alertDialogBuilder
                .setMessage("Impossible de se connecter au serveur")
                .setCancelable(true);

        // Creation du Dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Affichage du Dialog
        alertDialog.show();
    }

    //  Quand on presse la touche retour, on ne veut pas pouvoir revenir en arrière.
    //  (De base onBackPressed ferme l'activité et reviens à la précédente)
    @Override
    public void onBackPressed()
    {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Quitter l'application ?");

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });
        builder.setNegativeButton("Non", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public Context getContext()
    {
        return this;
    }

    public void bloquerBoutons()
    {
        bouton_avancer.setEnabled(false);
        bouton_reculer.setEnabled(false);
        bouton_droite.setEnabled(false);
        bouton_gauche.setEnabled(false);
        seekbar_vitesse.setEnabled(false);

        bouton_avancer.setPressed(false);
        bouton_reculer.setPressed(false);
        bouton_droite.setPressed(false);
        bouton_gauche.setPressed(false);
        seekbar_vitesse.setPressed(false);

        Connexion.envoyer_stop(this);
    }

    public void libererBoutons()
    {
        bouton_avancer.setEnabled(true);
        bouton_reculer.setEnabled(true);
        bouton_droite.setEnabled(true);
        bouton_gauche.setEnabled(true);
        seekbar_vitesse.setEnabled(true);
    }

    //  Gere en fonction de l'etat du robot (avance, recule, immobile...) les boutons qui sont
    //  clicables ou non
    public void gererBoutons()
    {
        if (direction == Direction.Immobile)
        {
            bouton_avancer.setEnabled(true);
            bouton_reculer.setEnabled(true);
            bouton_droite.setEnabled(true);
            bouton_gauche.setEnabled(true);
            seekbar_vitesse.setEnabled(true);
        }
        else if (direction == Direction.Avance)
        {
            bouton_avancer.setEnabled(true);
            bouton_reculer.setEnabled(false);
            bouton_droite.setEnabled(false);
            bouton_gauche.setEnabled(false);
            seekbar_vitesse.setEnabled(false);
        }
        else if (direction == Direction.Recule)
        {
            bouton_avancer.setEnabled(false);
            bouton_reculer.setEnabled(true);
            bouton_droite.setEnabled(false);
            bouton_gauche.setEnabled(false);
            seekbar_vitesse.setEnabled(false);
        }
        else if (direction == Direction.Tourne_droite)
        {
            bouton_avancer.setEnabled(false);
            bouton_reculer.setEnabled(false);
            bouton_droite.setEnabled(true);
            bouton_gauche.setEnabled(false);
            seekbar_vitesse.setEnabled(false);
        }
        else if (direction == Direction.Tourne_gauche)
        {
            bouton_avancer.setEnabled(false);
            bouton_reculer.setEnabled(false);
            bouton_droite.setEnabled(false);
            bouton_gauche.setEnabled(true);
            seekbar_vitesse.setEnabled(false);
        }
    }


    public void afficherDialogAttente(Object... arg)
    {
        Boolean attendre = (Boolean) arg[0];
        Long temps_attente = 0l;
        Long temps_controle = 0l;
        String couleur = "";

        if (arg.length == 4)
        {
            temps_attente = (Long)arg[1];
            temps_controle = (Long) arg[2];
            couleur = (String)arg[3];
        }

        if (attendre)
        {
            if(dialog_attente != null && dialog_attente.isShowing())
            {
                dialog_attente.cancel();
            }
            dialog_attente = new DialogAttente(this);
            dialog_attente.setCancelable(false);
            dialog_attente.show();
            bloquerBoutons();
        }
        else if (dialog_attente != null)
        {
            dialog_attente.cancel();
            afficher_dialog_accepter_controle(temps_attente, temps_controle, couleur);
        }
    }

    public void initialiserTimer(Long time)
    {
        timer_controle = (TextView) findViewById(R.id.timer_controle);

        timer = new CountDownTimer(time*1000 + 1000, 1000)
        {

            @Override
            public void onTick(long l)
            {
                timer_controle.setText("Temps de controle restant :"+Long.toString(l / 1000)+"s");
            }

            @Override
            public void onFinish()
            {
                timer_controle.setText("");
                afficherDialogRejouer();
            }
        }.start();

    }

    public void afficherDialogRejouer()
    {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(Html.fromHtml("Voulez vous avoir une chance de rejouer par la suite ?"));

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                timer.cancel();
                Connexion.envoyer_fin();
            }
        });
        builder.setNegativeButton("Non\n(Fermer l'application)", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Connexion.fermer_connexion();
                finish();
            }
        });

        dialog_rejouer = builder.create();
        dialog_rejouer.setCancelable(false);
        dialog_rejouer.show();
    }



}