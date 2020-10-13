package co.llanox.alacartaexpress.mobile.data;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import co.llanox.alacartaexpress.mobile.ACEErrorHandler;
import co.llanox.alacartaexpress.mobile.ErrorHandler;
import co.llanox.alacartaexpress.mobile.model.Order;
import co.llanox.alacartaexpress.mobile.model.OrderDetail;
import co.llanox.alacartaexpress.mobile.model.Product;

/**
 * Created by juangabrielgutierrez on 3/22/16.
 */
public class OrderDetailDataImpl implements OrderDetailData {

    private ErrorHandler errorHandler;

    public OrderDetailDataImpl( ) {
        this.errorHandler = ACEErrorHandler.getInstance();
    }

    @Override
    public void asyncFindAll(RetrievedDataListener<OrderDetail> listener, String... params) {

    }

    @Override
    public void asyncSave(SentDataListener<OrderDetail> listener, OrderDetail model, Object... params) {

    }


    @Override
    public void asyncFindOrderDetailByOrderId(String id,final RetrievedDataListener<OrderDetail> listener) {


        ParseQuery<ParseObject> innerQuery = ParseQuery.getQuery(Order.NAME);
        innerQuery.whereEqualTo("objectId", id);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(OrderDetail.NAME);
        query.whereMatchesQuery("order", innerQuery);


        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e != null){
                    errorHandler.onError(e);
                    listener.onError(e);
                    return;
                }

                 List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

                 List<RelationalDataParser<OrderDetail>> parsers = new ArrayList<RelationalDataParser<OrderDetail>>();
                 parsers.add(new OrderDetailProductFetcher());

                 OrderDetail orderDetail;

                for (ParseObject object : objects) {

                    try {
                      orderDetail = parseOrderDetail(object,parsers);
                    } catch (ParseException error) {
                        errorHandler.onError(e);
                        listener.onError(error);
                        return;
                    }
                    orderDetails.add(orderDetail);
                }

                listener.onRetrievedData(orderDetails);


            }
        });

    }

    private OrderDetail parseOrderDetail(ParseObject object, List<RelationalDataParser<OrderDetail>> parsers) throws ParseException {

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(object.getObjectId());
        orderDetail.setQuantity(object.getInt("quantity"));

        for (RelationalDataParser<OrderDetail> parser:parsers){
            parser.fetch(orderDetail,object);
        }


       return orderDetail;
    }


    class OrderDetailProductFetcher implements RelationalDataParser<OrderDetail>{


        @Override
        public void fetch(final OrderDetail orderDetail, ParseObject object) throws ParseException {

            ParseObject parseObject = object.getParseObject("product").fetchIfNeeded();
            Product product = ProductData.parseProduct(parseObject);
            orderDetail.setProduct(product);

        }
    }
}
