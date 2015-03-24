package com.theatre.ptutrobot.applicationpublic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.theatre.ptutrobot.applicationpublic.R;
import com.theatre.ptutrobot.applicationpublic.connexion.Connexion;
import com.theatre.ptutrobot.applicationpublic.notification.Alerte;

/**
 * Created by p1203391 on 26/01/2015.
 */

//  Cette classe correspond à la vue dialog_selection.xml
//  Cette boite de dialogue informe au joueur qui peut désormais accepter ou refuser
//  de controler le robot
public class DialogSelection extends Dialog
{
    //  L'activité appelant ce dialog (envoyé au constructeur)
    public Activity activity;

    //  Les boutons du dialog
    public Button bouton_accepter, bouton_refuser;

    //  Le timer de compte à rebours
    public CountDownTimer timer;

    //  La textView pour l'affichage du compte à rebours
    public TextView textview_timer;

    //  La durée totale du timer (envoyé au constructeur)
    public Integer max_time;

    //  Le temps de contrôle du robot
    public Long temps_controle;

    //  Couleur du robot
    public String couleur;

    //  Constructeur
    public DialogSelection(Activity a, Integer max_time, Long temps_controle, String couleur)
    {
        super(a);
        this.activity = a;
        this.max_time = max_time;
        this.temps_controle = temps_controle;
        this.couleur = couleur;

        Alerte.envoyer_notification(a, "Vous avez été sélectionné pour contrôler un robot !");
    }

    //  Appelé à la création du dialog
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_selection);

        //  Récupération des boutons depuis le xml
        bouton_accepter = (Button) findViewById(R.id.bouton_accepter);
        bouton_refuser = (Button) findViewById(R.id.bouton_refuser);

        //  Ajout des listeners
        bouton_accepter.setOnClickListener(bouton_accepter_listener);
        bouton_refuser.setOnClickListener(bouton_refuser_listener);

        //  Démarrage du timer
        init_timer();
    }

    //  Listener du bouton accepter
    View.OnClickListener bouton_accepter_listener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            accepter();
        }
    };

    //  Listener du bouton refuser
    View.OnClickListener bouton_refuser_listener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            refuser();
        }
    };

    //  Création du timer qui fait le décompte
    public void init_timer()
    {
        textview_timer = (TextView) findViewById(R.id.chronometre);

        timer = new CountDownTimer(max_time*1000+1000, 1000)
        {

            @Override
            public void onTick(long l)
            {
                textview_timer.setText(Long.toString(l / 1000));
            }

            @Override
            public void onFinish()
            {
                refuser();
            }
        }.start();
    }

    //  Méthode appelée dans le listener du bouton accepter lorsqu'on appuie dessus
    public void accepter()
    {
        Connexion.envoyer_accepter();
        timer.cancel();
        TextView tv = (TextView)(activity.findViewById(R.id.text_commande_robot));
        LinearLayout l = (LinearLayout)(activity.findViewById(R.id.layout_controle));

        tv.setText(activity.getString(R.string.controle_titre)+" "+couleur);
        if(couleur.equals("rouge"))
        {
            tv.setBackgroundColor(Color.parseColor("#ff0000"));
            l.setBackgroundColor(Color.parseColor("#880000"));
        }
        else if(couleur.equals("bleu"))
        {
            tv.setBackgroundColor(Color.parseColor("#0000ff"));
            l.setBackgroundColor(Color.parseColor("#000088"));
        }
        else if(couleur.equals("vert"))
        {
            tv.setBackgroundColor(Color.parseColor("#00ff00"));
            l.setBackgroundColor(Color.parseColor("#008800"));
        }
        else
        {
            tv.setBackgroundColor(Color.parseColor("#ffffff"));
            tv.setText(activity.getString(R.string.controle_titre)+" blanc");
        }


        ((ControleActivity)activity).initialiserTimer(this.temps_controle);
        ((ControleActivity)activity).libererBoutons();

        cancel();
    }

    //  Méthode appelée dans le listener du bouton refuser lorsqu'on appuie dessus
    public void refuser()
    {
        Connexion.envoyer_refuser();
        cancel();
    }
}

