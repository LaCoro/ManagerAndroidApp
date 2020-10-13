package co.llanox.alacartaexpress.mobile.data;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import co.llanox.alacartaexpress.mobile.model.Role;
import co.llanox.alacartaexpress.mobile.model.User;

/**
 * UserAuthentication logic to valid if user has permissions to
 * manage features for the application
 */
public class UserAuthenticationImpl implements UserAuthentication {


    public UserAuthenticationImpl( ) {

    }


    @Override
    public void logIn(final String username, final String password, final RetrievedDataListener<User> listener) {


        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {

                if (user != null) {
                    if (!hasRole(user, Role.ADMINISTRATOR)) {
                        logOut();
                        listener.onError(new NonAuthorizedUserException("The user is not an administrator"));
                        return;
                    }

                    returnAuthenticatedUser(listener, user);

                } else {
                    logOut();
                    listener.onError(new BackendException(e));
                }
            }
        });

    }

    private boolean hasRole(ParseUser user, Role role) {
        if (user.getACL().getRoleWriteAccess(role.getRoleName())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isAuthenticated() {

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null && user.isAuthenticated()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean hasRole(Role role) {
        ParseUser user = ParseUser.getCurrentUser();
        return hasRole(user,role);
    }

    @Override
    public void logOut() {
        ParseUser.logOutInBackground();
    }

    public void logOut(final SentDataListener listener){

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    listener.onError(new BackendException(e));
                    return;
                }
                listener.onResponse("OK");
            }
        });

    }

    private void returnAuthenticatedUser(RetrievedDataListener<User> listener, ParseUser user) {
        User userApp = new User();
        List<User> userResult = new ArrayList<User>();

        userApp.setId(user.getObjectId());
        userApp.setToken(user.getSessionToken());
        userResult.add(userApp);

        listener.onRetrievedData(userResult);
    }
}
