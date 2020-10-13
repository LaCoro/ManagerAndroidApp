package co.llanox.alacartaexpress.admin.validations;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.llanox.alacartaexpress.admin.R;
import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.ValidationResult;
import ua.org.zasadnyy.zvalidations.validations.Validation;

/**
 * Created by jgabrielgutierrez on 15-10-07.
 */
public class HasMinimumAge implements Validation {

    private SimpleDateFormat mFormat;
    private Context mContext;
    private int minimumAge;

    public HasMinimumAge(Context ctx, SimpleDateFormat format, int minimumAge) {
        this.mContext = ctx;
        this.mFormat = format;
        this.minimumAge = minimumAge;
    }

    public static Validation build(Context context, SimpleDateFormat format, int minimumAge) {
        return new HasMinimumAge(context, format, minimumAge);
    }

    @Override
    public ValidationResult validate(Field field) {


        boolean isValid = false;
        try {

            Calendar cal = Calendar.getInstance();
            String sDate = field.getTextView().getText().toString();
            cal.setTime(mFormat.parse(sDate));

            int bdYear = cal.get(Calendar.YEAR);
            cal = Calendar.getInstance();

            int currentYear = cal.get(Calendar.YEAR);
            int age = currentYear - bdYear;

            isValid = age >= minimumAge;


        } catch (ParseException e) {
            isValid = false;
        }

        return isValid ?
                ValidationResult.buildSuccess(field.getTextView())
                : ValidationResult.buildFailed(field.getTextView(),
                mContext.getString(R.string.validation_err_not_allowed_age));


    }
}
