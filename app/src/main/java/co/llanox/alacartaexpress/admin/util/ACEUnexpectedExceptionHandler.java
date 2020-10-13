package co.llanox.alacartaexpress.admin.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import co.llanox.alacartaexpress.admin.UICommands;

/**
 * Created by llanox on 5/19/16.
 */
public class ACEUnexpectedExceptionHandler implements Thread.UncaughtExceptionHandler {
    Activity activity;

    public ACEUnexpectedExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {



            Intent intent = new Intent(activity.getApplication(), UICommands.ENTRY_POINT);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    activity.getApplication().getBaseContext(), 0, intent,PendingIntent.FLAG_ONE_SHOT);

            //Following code will restart your application after 2 seconds
            AlarmManager mgr = (AlarmManager) activity.getApplication().getBaseContext()
                    .getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    pendingIntent);

            //This will finish your activity manually
            activity.finish();
            //This will stop your application and take out from it.
            System.exit(2);


    }
}
