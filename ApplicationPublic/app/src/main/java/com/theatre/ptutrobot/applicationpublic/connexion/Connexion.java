package com.theatre.ptutrobot.applicationpublic.connexion;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.theatre.ptutrobot.applicationpublic.activities.ControleActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by p1203391 on 26/01/2015.
 */
public class Connexion
{
    //  L'adresse de connexion au serveur
    public final static Integer numero_port = 3000;
    public static String hostname = "";

    //  Direction du robot
    public final static String avancer = "avancer";
    public final static String reculer = "reculer";
    public final static String gauche = "gauche";
    public final static String droite = "droite";

    //  Socket
    public static Socket socket = null;
    public static PrintWriter out = null;
    public static BufferedReader in = null;

    //  Erreur de connexion
    private static Boolean network_error = false;

    //  Constructeur en private : On ne veut pas pouvoir instancier cette classe.
    private Connexion(){}

    //  Liste des fonctions necessitant une connexion : Appel aux threads
    public static void envoyer_direction(Context c, String direction, Integer vitesse)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");

            JSONObject details = new JSONObject();
            details.put("action",direction);
            details.put("vitesse",vitesse);

            jsonObject.put("detail",details);

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    //  Envoi un stop au serveur
    public static void envoyer_stop(Context c)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");

            JSONObject details = new JSONObject();
            details.put("action","stop");

            jsonObject.put("detail",details);

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    //  Permet de prendre une photo
    public static void prendre_photo(Context c)
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");

            JSONObject details = new JSONObject();
            details.put("action","photo");

            jsonObject.put("detail",details);

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    //  Permet d'ouvrir une connexion : Retourne true en cas d'erreur et false en cas de réussite
    public static Boolean ouvrir_connexion()
    {
        try
        {
            //  Ouvrir la connexion
            new TaskOuvrirConnexion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();

            //  Envoyer confirmation
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");
            jsonObject.put("action", "init");

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch(Exception ex)
        {
            network_error = true;
        }

        Boolean erreur = network_error;
        network_error = false;
        return erreur;
    }

    public static void se_mettre_en_attendre(ControleActivity activity)
    {
        try
        {
            new TaskAttenteReception().execute(activity);
        }
        catch(Exception ex)
        {
        }
    }

    //  Permet de fermer une connexion : Retourne true en cas d'erreur et false en cas de réussite
    public static Boolean fermer_connexion()
    {
        try
        {
            Connexion.envoyer_fin();
            new TaskFermerConnexion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        }
        catch(Exception ex)
        {
            network_error = true;
        }

        Boolean erreur = network_error;
        network_error = false;
        return erreur;
    }

    //  Permet d'accepter le controle du robot
    public static void envoyer_accepter()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");

            JSONObject details = new JSONObject();
            details.put("action","accepter");

            jsonObject.put("detail",details);

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    //  Permet de refuser le controle du robot
    public static void envoyer_refuser()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");

            JSONObject details = new JSONObject();
            details.put("action","refuser");

            jsonObject.put("detail",details);

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    //  Permet de refuser le controle du robot
    public static void envoyer_fin()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idModule", "public");

            JSONObject details = new JSONObject();
            details.put("action","fin");

            jsonObject.put("detail",details);

            new TaskEnvoyerTexte().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject.toString()).get();
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

    //  Liste des ASyncTask (Threads appelé par les méthodes du dessus)

    //  Thread de connexion
    private static class TaskOuvrirConnexion extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... arg)
        {
            super.onProgressUpdate(arg);
        }

        @Override
        protected Void doInBackground(Void... arg)
        {
            try
            {
                socket = new Socket();
                socket.connect(new InetSocketAddress(getServerIpAdress(), numero_port), 3000);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());
            }
            catch (Exception ex)
            {
                network_error = true;
            }

            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }

    //  Thread de fermeture de la connexion
    private static class TaskFermerConnexion extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... arg)
        {
            super.onProgressUpdate(arg);
        }

        @Override
        protected Void doInBackground(Void... arg)
        {
            try
            {
                socket.close();
                out = null;
                socket = null;
            }
            catch (Exception ex)
            {
                network_error = true;
            }

            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }

    //  Thread d'envoi de String au serveur
    private static class TaskEnvoyerTexte extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... arg)
        {
            super.onProgressUpdate(arg);
        }

        @Override
        protected Void doInBackground(String... arg)
        {
            try
            {
                out.println(arg[0]);
                out.flush();
            }
            catch(Exception ex)
            {
                System.out.println(ex);
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }

    //  Thread d'attente de reception
    private static class TaskAttenteReception extends AsyncTask<ControleActivity, Object, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object... arg)
        {
            super.onProgressUpdate(arg);
            if(arg.length == 5)
            {
                ((ControleActivity)arg[0]).afficherDialogAttente(arg[1], arg[2], arg[3], arg[4]);
            }
            else
            {
                ((ControleActivity)arg[0]).afficherDialogAttente((Boolean)arg[1]);
            }
        }

        @Override
        protected Void doInBackground(ControleActivity... arg)
        {
            while(true)
            {
                try
                {
                    if(in.ready())
                    {
                        String line = in.readLine();
                        JSONObject jsonObject = new JSONObject(line);

                        if(jsonObject.has("secondeAttente") && jsonObject.has("secondeControle"))
                        {
                            publishProgress(arg[0],(Boolean) jsonObject.get("attente"), Long.parseLong(jsonObject.get("secondeAttente").toString()), Long.parseLong(jsonObject.get("secondeControle").toString()), jsonObject.get("couleurRobot"));
                        }
                        else
                        {
                            publishProgress(arg[0], (Boolean) jsonObject.get("attente"));
                        }
                    }

                }
                //  Si on ne reçoit pas de réponse
                catch (SocketTimeoutException socketTimeoutException )
                {
                    continue;
                }
                //  Si on perd la connexion
                catch (Exception ex)
                {
                    network_error = true;
                }
            }
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
        }
    }

    //  Retourne l'adresse IP du mobile
    public static String getIpAdress(Context c)
    {
        WifiManager wifiManager = (WifiManager)  c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    //  Retourne l'adresse IP du serveur
    public static String getServerIpAdress()
    {
        /*
        try
        {
            InetAddress address = InetAddress.getByName(hostname);
            return address.getHostAddress();
        }
        catch(Exception ex)
        {
            return null;
        }
        */
        return hostname;
    }
}
