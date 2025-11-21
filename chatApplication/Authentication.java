package chatApplication;

import java.io.File;
import java.util.ArrayList;

public class Authentication {
    ArrayList<User> users;
    Storage storage;

    public Authentication(Storage storage) {
        this.storage = storage;
        this.users = new ArrayList<User>();
    }

    public void reloadUsersFromStorage() {
        try {
            ArrayList<User> loaded = storage.getUser();
            if (loaded != null)
                this.users = loaded;
            else
                this.users = new ArrayList<User>();
        } catch (Exception e) {
            System.out.println(Main.RED + "⚠️ Failed to load users: " + e.getMessage() + Main.RESET);
            this.users = new ArrayList<User>();
        }
    }

    public User signUp(String uName, String phone, String pass, boolean isPrivate) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(uName)) {
                System.out.println(Main.RED + "⚠️ Username already exists." + Main.RESET);
                return null;
            }
            if (u.getPhone().equals(phone)) {
                System.out.println(Main.RED + "⚠️ Phone number already registered." + Main.RESET);
                return null;
            }
        }
        User n = new User(uName, phone, pass, isPrivate);
        users.add(n);
        try {
            storage.saveUser(n);
        } catch (Exception e) {
            System.out.println(Main.RED + "⚠️ Error saving user: " + e.getMessage() + Main.RESET);
        }
        return n;
    }

    public User login(String uName, String pass) {
        for (User u : users) {
            if ((u.getUsername().equalsIgnoreCase(uName) || u.getPhone().equals(uName))
                    && u.getPassword().equals(pass)) {
                System.out.println(Main.GREEN + "✅ Login successful." + Main.RESET);
                return u;
            }
        }
        System.out.println(Main.RED + "❌ Invalid username or password." + Main.RESET);
        return null;
    }

    public void displayPublicUsers() {
        System.out.println(Main.CYAN + "---------- Registered Users ----------" + Main.RESET);
        boolean found = false;
        for (User u : users) {
            if (!u.isPrivate()) {
                System.out.println("---> " + u.getUsername() + " (" + u.getPhone() + ")");
                found = true;
            }
        }
        if (!found)
            System.out.println(Main.YELLOW + "😕 No users registered." + Main.RESET);
    }

    public void displayUsersFor(User viewer) {
        System.out.println(Main.CYAN + "---- Registered Users ----" + Main.RESET);
        boolean found = false;
        for (User u : users) {
            if ((!u.isPrivate() || hasMessage(viewer, u)) && !u.getUsername().equalsIgnoreCase(viewer.getUsername())) {
                System.out.println("---> " + u.getUsername() + " (" + (u.isPrivate() ? "Private" : "Public") + ")");
                found = true;
            }
        }
        if (!found)
            System.out.println(Main.YELLOW + "😕 No users to show." + Main.RESET);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public Storage getStorage() {
        return storage;
    }

    public User findUserByName(String name) {
        for (User u : users)
            if (u.getUsername().equalsIgnoreCase(name))
                return u;
        return null;
    }

    public boolean isUserExistsWithPhone(String phone) {
        for (User user : users) {
            if (user.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserExistsWithName(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMessage(User user, User other) {
        String path = "Chats/" + user.getUsername() + "/" + other.getUsername() + ".txt";
        File f = new File(path);

        if (f.exists()) {
            return true;
        }
        return false;
    }
}
