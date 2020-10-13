package co.llanox.alacartaexpress.mobile.data;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import co.llanox.alacartaexpress.mobile.ACEErrorHandler;
import co.llanox.alacartaexpress.mobile.ACEUtil;
import co.llanox.alacartaexpress.mobile.ErrorHandler;
import co.llanox.alacartaexpress.mobile.model.Store;

/**
 * Created by jgabrielgutierrez on 15-07-22.
 */
public class StoreData implements ObjectData<Store> {

    public static final String STORE_OBJECT_NAME = "Store";

    private ErrorHandler errorHandler;
    public StoreData() {
        errorHandler = ACEErrorHandler.getInstance();
    }



    @Override
    public void asyncFindAll(final RetrievedDataListener<Store> listener, String... params) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(STORE_OBJECT_NAME);
        query.whereEqualTo("active", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {



                if (e == null) {

                    List<Store> stores = new ArrayList<Store>();
                    Store store = null;

                    for (ParseObject object : objects) {
                        store = parseStore(object);
                        stores.add(store);
                    }

                    listener.onRetrievedData(stores);

                } else {
                    errorHandler.onError(e);
                    listener.onError(e);
                }
            }
        });

    }

    public static Store parseStore(ParseObject object) {
        Store store;
        String state = object.containsKey("status")?object.getString("status"):"";
        boolean isOpen = false;

        if(Store.StoreStates.OPENED.equalsIgnoreCase(state)) {

            String[] openTime = object.getString("openAt").split(":");
            String[] closeTime = object.getString("closeAt").split(":");
            isOpen = ACEUtil.isCurrentTimeInRange(openTime, closeTime);

        }


        BigDecimal deliveryCost = new BigDecimal(object.getDouble("deliveryCost"));


        store = new Store(object.getObjectId(),
                object.getString("name"),
                object.getString("description"),
                object.getString("address"),
                object.getString("logo"),
                (ArrayList) object.get("searchTags"),
                object.getString("openAt"),
                object.getString("closeAt"),
                isOpen,
                deliveryCost,
                object.getString("mobile"),
                object.getString("city")

        );
        return store;
    }

    @Override
    public void asyncSave(SentDataListener<Store> listener, Store model, Object... params) {

    }


}
