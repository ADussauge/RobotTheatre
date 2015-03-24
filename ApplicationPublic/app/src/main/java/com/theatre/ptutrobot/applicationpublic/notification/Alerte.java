package com.theatre.ptutrobot.applicationpublic.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.theatre.ptutrobot.applicationpublic.R;

/**
 * Created by Aurélien on 28/01/2015.
 */
public class Alerte
{
    private Alerte()
    {
    }

    public static void envoyer_notification(Context c, String message)
    {
        Notification noti = new Notification.Builder(c)
                .setContentTitle("Robots Théâtre")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher))
                .getNotification();


        NotificationManager mNotifyMgr = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.notify(1, noti);

        Uri son = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Ringtone r = RingtoneManager.getRingtone(c, son);
        r.play();

        Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(2000);
    }
}
