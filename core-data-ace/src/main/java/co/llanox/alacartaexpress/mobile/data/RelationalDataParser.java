package co.llanox.alacartaexpress.mobile.data;

import com.parse.ParseException;
import com.parse.ParseObject;


/**
 * Created by llanox on 3/21/16.
 */
public interface RelationalDataParser<T> {
    void fetch(T model, ParseObject object) throws ParseException;
}
