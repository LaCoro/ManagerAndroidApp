package co.llanox.alacartaexpress.mobile.data;

import co.llanox.alacartaexpress.mobile.model.Role;
import co.llanox.alacartaexpress.mobile.model.User;

/**
 * @author llanox
 */
public interface UserAuthentication {

    void logIn(String username, String password, RetrievedDataListener<User> retrievedDataListener);
    boolean isAuthenticated();
    boolean hasRole(Role role);
    void logOut();
    void logOut(SentDataListener listener);
}
