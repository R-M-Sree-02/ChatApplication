package chatApplication;

import java.util.ArrayList;

public abstract class Storage {
    public abstract void saveUser(User user);

    public abstract ArrayList<User> getUser();
}
