package co.llanox.alacartaexpress.mobile;

import android.content.Context;

import com.parse.ParseUser;

import java.util.Calendar;

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
public class ACEUtil {


    public ACEUtil() {
    }

    public static String getCurrentUserID(){

        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null) return user.getUsername();

        return "no_registered_user";
    }

    public static boolean isCurrentTimeInRange(String[] openAt, String[] closeAt) {

        Calendar cal = Calendar.getInstance();

        int hourOfCloseTime = Integer.valueOf(closeAt[0]);
        int minuteOfCloseTime = Integer.valueOf(closeAt[1]);

        int hourOfOpenTime = Integer.valueOf(openAt[0]);
        int minuteOfOpenTime = Integer.valueOf(openAt[1]);


        int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinute = cal.get(Calendar.MINUTE);

        //For store hours when are in a range between two different days
        int diff = hourOfOpenTime - hourOfCloseTime;

        if (diff > 0) {
            hourOfCloseTime += 24;
        }

        if (currentHourOfDay > hourOfCloseTime || currentHourOfDay < hourOfOpenTime)
            return false;

        if (currentHourOfDay == hourOfOpenTime && currentMinute < minuteOfOpenTime)
            return false;

        if (currentHourOfDay == hourOfCloseTime && currentMinute > minuteOfCloseTime)
            return false;


        return true;
    }


}
