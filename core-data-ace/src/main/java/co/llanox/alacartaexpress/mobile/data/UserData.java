package co.llanox.alacartaexpress.mobile.data;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import co.llanox.alacartaexpress.mobile.ACEErrorHandler;
import co.llanox.alacartaexpress.mobile.ErrorHandler;
import co.llanox.alacartaexpress.mobile.model.User;

/**
 * Created by jgabrielgutierrez on 15-09-30.
 */
public class UserData implements ObjectData<User> {

    private ErrorHandler errorHandler;

    public UserData() {
        errorHandler = ACEErrorHandler.getInstance();
    }


    @Override
    public void asyncFindAll(RetrievedDataListener<User> listener, String... params) {


    }


    public void asyncSave(final SentDataListener<User> listener, final User userAce, Object... params) {


        ParseUser user = new ParseUser();
        user.setUsername(userAce.getEmail());
        user.setPassword(userAce.getEmail());
        user.setEmail(userAce.getEmail());
        user.put("phone", userAce.getPhone());
        user.put("mobile.mobile.core", userAce.getPhone());
        user.put("fullname", userAce.getFullname());
        user.put("birthday", userAce.getBirthday());


        user.signUpInBackground(new SignUpCallback() {

            public void done(ParseException e) {
                if (e == null) {
                    listener.onResponse(userAce);
                } else {
                    errorHandler.onError(e);
                    listener.onError(e);
                }
            }
        });

    }

    public void loginUser(final SentDataListener loginUserListener, User user) {


        ParseUser.logInInBackground(user.getEmail(), user.getEmail(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    loginUserListener.onResponse(user);
                } else {
                    errorHandler.onError(e);
                    loginUserListener.onError(e);
                }
            }
        });

    }

    public User getCurrentUser() throws ParseException {

        User currentUser = null;
        ParseUser user = ParseUser.getCurrentUser().fetch();

        if (user != null) {
            currentUser = parseUser(user);// currentUser = new User(user.getObjectId(), user.get("fullname").toString(), user.get("phone").toString(), user.getEmail(), user.getDate("birthday"), user.getParseObject("managed_store").getObjectId(), user.getString("city"));
        }
        return currentUser;
    }

    private User parseUser(ParseUser parseUser) {
        User user = new User();
        user.setId(parseUser.getObjectId());
        user.setFullname(parseUser.getString("fullname"));
        user.setPhone(parseUser.getString("phone"));
        user.setCity(parseUser.getString("city"));
        user.setEmail(parseUser.getString("email"));
        user.setBirthday(parseUser.getDate("birthday"));
        ParseObject managedStoreObject = parseUser.getParseObject("managed_store");
        user.setManagedStoreId(managedStoreObject != null ? managedStoreObject.getObjectId() : "");

        return user;
    }
}
