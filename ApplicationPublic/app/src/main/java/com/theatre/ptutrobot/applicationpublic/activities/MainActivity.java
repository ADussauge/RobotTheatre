package com.theatre.ptutrobot.applicationpublic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.theatre.ptutrobot.applicationpublic.R;
import com.theatre.ptutrobot.applicationpublic.connexion.Connexion;

import java.io.PrintWriter;
import java.net.Socket;

//  Classe correspondant au code executé dans la vue activity_main.xml
//  Cette vue permet l'affichage des instructions et la classe ci-dessous
//  permet d'initialiser la connexion.
public class MainActivity extends Activity
{

    @Override
    //  Méthode appelée au lancement de l'application
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bouton_fermer = (Button) findViewById(R.id.bouton_fermer);
        bouton_fermer.setOnClickListener(boutonFermerListener);
    }

    //  Code retourné à la fin de l'activité de contrôle
    static final int FIN_CONTROLE = 1;

    //  Passer à la fenêtre de contrôle du robot
    public void intentControleActivity()
    {
        Intent intent = new Intent(this, ControleActivity.class);
        intent.putExtra("pid",android.os.Process.myPid());
        startActivityForResult(intent, FIN_CONTROLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        finish();
    }

    //  Listener du bouton fermer
    View.OnClickListener boutonFermerListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            // Si ouvrir connexion retourne vrai, on a une erreur donc on affiche une dialog erreur
            EditText text_ip = (EditText) findViewById(R.id.text_ip);
            Connexion.hostname = "192.168.1."+text_ip.getText().toString();
            if(Connexion.ouvrir_connexion())
            {
                afficherDialogErreur();
            }
            else
            {
                intentControleActivity();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        }
    };

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

}
