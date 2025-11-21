package chatApplication;

import java.util.ArrayList;

public class ArrayStorage extends Storage {
    ArrayList<User> users;

    public ArrayStorage() {
        users = new ArrayList<User>();
    }

    public void saveUser(User user) {
        if (user == null)
            return;
        users.add(user);
    }

    public ArrayList<User> getUser() {
        return users;
    }
}
