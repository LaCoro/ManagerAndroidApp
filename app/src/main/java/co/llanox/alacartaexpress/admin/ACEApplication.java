package co.llanox.alacartaexpress.admin;

import android.app.Application;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.parse.Parse;

import co.llanox.alacartaexpress.mobile.ACEErrorHandler;
import co.llanox.alacartaexpress.mobile.ApplicationSessionHelper;

/**
 * Created by jgabrielgutierrez on 15-09-01.
 */
public class ACEApplication extends Application {

    private ApplicationSessionHelper applicationSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.Configuration.Builder builder = new Parse.Configuration.Builder(this).
                server(getString(R.string.parse_base_url)).
                clientKey(getString(R.string.parse_client_id)).
                applicationId(getString(R.string.parse_application_id));

        Parse.initialize(builder.build());
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        ACEErrorHandler.getInstance().updateCurrentUserID();
        applicationSession = ApplicationSessionHelper.getInstance(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    public ApplicationSessionHelper getApplicationSession() {
        return applicationSession;
    }
}
