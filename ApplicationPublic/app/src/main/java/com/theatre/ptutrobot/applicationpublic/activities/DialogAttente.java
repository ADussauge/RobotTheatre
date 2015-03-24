package com.theatre.ptutrobot.applicationpublic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.*;
import android.os.Process;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.theatre.ptutrobot.applicationpublic.R;
import com.theatre.ptutrobot.applicationpublic.connexion.Connexion;

/**
 * Created by p1203391 on 26/01/2015.
 */

//  Cette classe correspond à la vue dialog_selection.xml
//  Cette boite de dialogue informe au joueur qui peut désormais accepter ou refuser
//  de controler le robot
public class DialogAttente extends Dialog
{
    //  L'activité appelant ce dialog (envoyé au constructeur)
    public Activity activity;

    //  Constructeur
    public DialogAttente(Activity a)
    {
        super(a);
        this.activity = a;
    }

    //  Appelé à la création du dialog
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_attente);
    }


    @Override
    public void onBackPressed()
    {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Quitter l'application ?");

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                Connexion.fermer_connexion();
                android.os.Process.sendSignal(Process.myPid(), Process.SIGNAL_KILL);
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
        dialog.show();
    }

}



