package chatApplication;

public class User extends Person {
    String password;
    boolean isPrivate;
    

    public User(String name, String phone, String password, boolean isPrivate) {
        super(name, phone);
        this.password = password;
        this.isPrivate = isPrivate;
    }

    public String getUsername() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean p) {
        this.isPrivate = p;
    }

    public String userDetails() {
        return name + "," + phone + "," + password + "," + isPrivate;
    }

    public static User userDetailsByLine(String line) {
        if (line == null)
            return null;
        String[] p = line.split(",", -1);
        if (p.length >= 4) {
            return new User(p[0], p[1], p[2], Boolean.parseBoolean(p[3]));
        } else {
            return null;
        }
    }

    public String toString() {
        return name + " (" + (isPrivate ? "Private" : "Public") + ") - " + phone;
    }

}
