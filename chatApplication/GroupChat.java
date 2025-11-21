package chatApplication;

import java.util.ArrayList;

public class GroupChat {
    public static ArrayList<GroupChat> allGroups = new ArrayList<GroupChat>();

    String groupName;
    User admin;
    boolean isPrivate;
    ArrayList<User> members;
    ArrayList<User> subAdmins;
    FileStorage storage;
    ArrayList<String> joinRequests = new ArrayList<String>();

    public GroupChat(String groupName, User admin, boolean isPrivate, FileStorage storage) {
        this.groupName = groupName;
        this.admin = admin;
        this.isPrivate = isPrivate;
        this.storage = storage;
        this.members = new ArrayList<User>();
        this.subAdmins = new ArrayList<User>();
        this.members.add(admin);
        allGroups.add(this);
        if (storage != null)
            storage.createGroupFile(groupName);
    }

    public GroupChat(String groupName, User admin, boolean isPrivate, ArrayList<User> subAdmins,
            ArrayList<User> members, FileStorage storage) {
        this.groupName = groupName;
        this.admin = admin;
        this.isPrivate = isPrivate;
        this.storage = storage;
        this.subAdmins = (subAdmins != null) ? subAdmins : new ArrayList<User>();
        this.members = (members != null) ? members : new ArrayList<User>();
        if (!this.members.contains(admin))
            this.members.add(admin);
        allGroups.add(this);
        if (storage != null)
            storage.createGroupFile(groupName);
    }

    public boolean isMember(User user) {
        if (user == null)
            return false;
        for (User m : members)
            if (m.getUsername().equalsIgnoreCase(user.getUsername()))
                return true;
        return false;
    }

    public boolean isSubAdmin(User user) {
        if (user == null)
            return false;
        for (User s : subAdmins)
            if (s.getUsername().equalsIgnoreCase(user.getUsername()))
                return true;
        return false;
    }

    public void displayMessages() {
        ArrayList<String> msgs = storage.loadGroupMessages(groupName);
        if (msgs == null || msgs.isEmpty()) {
            System.out.println(Main.YELLOW + "Say 'HI 👋' to Start " + groupName + Main.RESET);
            return;
        }
        for (String m : msgs)
            System.out.println(m);
    }

    public void sendMessage(User sender, String content) {
        storage.saveGroupMessage(groupName, sender.getUsername(), content);
    }

    public void addJoinRequest(User user, FileStorage storage) {
        String entry = user.getUsername() + "|" + storage.getTimestamp();

        for (String request : joinRequests)
            if (request.startsWith(user.getUsername() + "|")) {
                System.out.println(
                        Main.CYAN + "😅 You Send the Request already, wait for a while to accept" + Main.RESET);
                return;
            }
        System.out.println(Main.GREEN + "✅ Request Sent Successfully!." + Main.RESET);
        joinRequests.add(entry);
    }

    public boolean adminSettings(User current, Authentication auth) {
        Input input = new Input();
        boolean isAdmin = (current != null && admin != null
                && current.getUsername().equalsIgnoreCase(admin.getUsername()));

        while (true) {
            System.out.println(Main.CYAN + "\n⚙️ Admin Settings for '" + groupName + "':" + Main.RESET
                    + "\n1. View Members"
                    + "\n2. View Requests"
                    + "\n3. Add"
                    + "\n4. Remove"
                    + "\n5. Delete Group"
                    + "\n6. Exit");

            int choice = input.getChoice();

            switch (choice) {
                case 1:
                    System.out.println(Main.CYAN + "------ All Members ------" + Main.RESET);
                    if (members.size() == 1)
                        System.out.println(Main.BLUE + "You are the only member." + Main.RESET);
                    for (User mem : members) {
                        if (!mem.getName().equalsIgnoreCase(admin.getName()))
                            System.out.println("---> " + mem.getName());
                    }
                    break;
                case 2:
                    System.out.println(Main.CYAN + "\n--- Join Requests ---" + Main.RESET);

                    if (joinRequests.isEmpty()) {
                        System.out.println(Main.YELLOW + "📭 No requests." + Main.RESET);
                        continue;
                    }

                    for (String request : joinRequests) {
                        String[] x = request.split("\\|");
                        System.out.println("👤 User: " + x[0] + " | ⏰ Time: " + x[1]);
                    }
                    break;
                case 3:
                    while (true) {
                        System.out.println(Main.CYAN + "\nAdd:" + Main.RESET + "\n1. Add Subadmin" + "\n2. Add Member"
                                + "\n3. Exit");
                        int choice2 = input.getChoice();
                        if (choice2 == 1) {
                            if (!isAdmin) {
                                System.out.println(Main.RED + "⚠️ Only admin can add subadmins." + Main.RESET);
                                continue;
                            }
                            for (User user : auth.users) {
                                if (members.contains(user) && !subAdmins.contains(user)
                                        && !admin.getName().equalsIgnoreCase(user.getName()))
                                    System.out.println("---> " + user.getName());
                            }
                            System.out.println(Main.YELLOW + "---> type no to avoid." + Main.RESET);
                            String uName = input.getString("Enter username to make subadmin: ");
                            if (uName.equalsIgnoreCase("no")) {
                                System.out.println(Main.YELLOW + "⬅️ Returning to menu..." + Main.RESET);
                                continue;
                            }
                            User user = auth.findUserByName(uName);
                            if (user == null) {
                                System.out.println(Main.RED + "⚠️ User not found." + Main.RESET);
                                continue;
                            }
                            subAdmins.add(user);
                            System.out.println(Main.GREEN + "✅ Subadmin added." + Main.RESET);
                            storeGroup();
                        } else if (choice2 == 2) {
                            if (auth.users.size() == members.size()) {
                                System.out.println(Main.RED + "😕 No Users to Add" + Main.RESET);
                                break;
                            }
                            for (User user : auth.users) {
                                if (!subAdmins.contains(user) && !members.contains(user))
                                    System.out.println("---> " + user.getName());
                            }
                            System.out.println(Main.YELLOW + "---> type no to avoid." + Main.RESET);
                            String uName = input.getString("Enter username to add as member: ");
                            if (uName.equalsIgnoreCase("no")) {
                                System.out.println(Main.YELLOW + "⬅️ Returning to menu..." + Main.RESET);
                                continue;
                            }
                            User user = auth.findUserByName(uName);
                            if (user == null) {
                                System.out.println(Main.RED + "⚠️ User not found." + Main.RESET);
                                continue;
                            }
                            if (!isMember(user)) {
                                members.add(user);
                                System.out.println(Main.GREEN + "✅ Member added." + Main.RESET);
                                storeGroup();
                            } else {
                                System.out.println(Main.YELLOW + "👤 User is already a member." + Main.RESET);
                            }
                        } else if (choice2 == 3) {
                            break;
                        } else {
                            System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
                        }
                    }
                    break;

                case 4:
                    while (true) {
                        System.out.println(Main.CYAN + "\nRemove:" + Main.RESET + "\n1. Remove Subadmin"
                                + "\n2. Remove Member" + "\n3. Exit");
                        int choice2 = input.getChoice();
                        if (choice2 == 1) {
                            if (!isAdmin) {
                                System.out.println(Main.RED + "⚠️ Only admin can remove subadmins." + Main.RESET);
                                continue;
                            }
                            for (User user : auth.users) {
                                if (subAdmins.contains(user)) {
                                    System.out.println("---> " + user.getName());
                                }
                            }
                            System.out.println(Main.YELLOW + "---> type no to avoid." + Main.RESET);
                            String uName = input.getString("Enter username to remove from subadmins: ");
                            if (uName.equalsIgnoreCase("no")) {
                                System.out.println(Main.YELLOW + "⬅️ Returning to menu..." + Main.RESET);
                                continue;
                            }
                            boolean removed = false;
                            for (int i = 0; i < subAdmins.size(); i++) {
                                if (subAdmins.get(i).getUsername().equalsIgnoreCase(uName)) {
                                    subAdmins.remove(i);
                                    removed = true;
                                    break;
                                }
                            }
                            if (removed) {
                                System.out.println(Main.GREEN + "✅ Subadmin removed." + Main.RESET);
                                storeGroup();
                            }
                        } else if (choice2 == 2) {
                            if (members.size() == 1) {
                                System.out.println(Main.RED + "No Members to Remove" + Main.RESET);
                            }
                            for (User user : auth.users) {
                                if (!admin.getName().equalsIgnoreCase(user.getName()) && !subAdmins.contains(user)
                                        && members.contains(user)) {
                                    System.out.println("---> " + user.getName());
                                }
                            }
                            System.out.println(Main.YELLOW + "---> type no to avoid." + Main.RESET);
                            String uName = input.getString("Enter username to remove from members: ");

                            if (uName.equalsIgnoreCase("no")) {
                                System.out.println(Main.YELLOW + "⬅️ Returning to menu..." + Main.RESET);
                                continue;
                            }
                            User user = auth.findUserByName(uName);
                            if (user == null) {
                                System.out.println(Main.RED + "⚠️ User not found." + Main.RESET);
                                continue;
                            }
                            boolean removed = false;
                            for (int i = 0; i < members.size(); i++) {
                                if (members.get(i).getUsername().equalsIgnoreCase(uName)) {
                                    members.remove(i);
                                    removed = true;
                                    break;
                                }
                            }
                            subAdmins.remove(user);
                            if (removed) {
                                System.out.println(Main.GREEN + "✅ Member removed." + Main.RESET);
                                storeGroup();
                            } else
                                System.out.println(Main.YELLOW + "User is not a member." + Main.RESET);
                        } else if (choice2 == 3)
                            break;
                        else
                            System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
                    }
                    break;

                case 5:
                    if (!isAdmin) {
                        System.out.println(Main.RED + "⚠️ Only admin can delete the group." + Main.RESET);
                        break;
                    }
                    String yORn = input.getString("Are you sure you want to delete this group? (yes/no): ").toLowerCase();
                    if (yORn.startsWith("y")) {
                        if (storage != null)
                            storage.deleteGroup(groupName);
                        for (int i = 0; i < allGroups.size(); i++)
                            if (allGroups.get(i).groupName.equalsIgnoreCase(groupName)) {
                                allGroups.remove(i);
                                break;
                            }
                        System.out.println(Main.GREEN + "✅ Group deleted." + Main.RESET);
                        storeGroup();
                        return true;
                    } else
                        System.out.println(Main.YELLOW + "Aborted delete." + Main.RESET);
                    break;

                case 6:
                    return false;
                default:
                    System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
            }
            // return false;
        }
    }

    void storeGroup() {
        if (storage != null)
            storage.saveGroups(allGroups);
    }
}
