package chatApplication;

import java.util.ArrayList;

public class ChatApplication {
    Authentication auth;
    FileStorage storage;
    Input input;

    public ChatApplication(Authentication auth, FileStorage storage) {
        this.auth = auth;
        this.storage = storage;
        this.input = new Input();
    }

    public void loadGroupsAtStart() {
        ArrayList<GroupChat> loaded = storage.loadGroups(auth);
        if (loaded != null) {
            GroupChat.allGroups.clear();
            for (GroupChat g : loaded)
                GroupChat.allGroups.add(g);
        }
    }

    public void startChatSession(User user) {
        boolean inSession = true;
        while (inSession) {
            System.out.println(Main.CYAN + "\n--- 👋 Welcome, " + user.getUsername() + " ---" + Main.RESET
                    + "\n1. Personal Chat" + "\n2. Group Chat" + "\n3. Contact" + "\n4. Logout");

            byte choice = input.getChoice();

            switch (choice) {
                case 1:
                    personalChat(user);
                    break;
                case 2:
                    groupFlow(user);
                    break;
                case 3:
                    auth.displayUsersFor(user);
                    break;
                case 4:
                    System.out.println(
                            Main.GREEN + " --------------------- Logged out. --------------------- " + Main.RESET);
                    inSession = false;
                    break;
                default:
                    System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
            }
        }
    }

    void personalChat(User user) {
        String to = input.getString("Enter receiver username: ");
        if (user.getUsername().equalsIgnoreCase(to)) {
            System.out.println(Main.RED + "🚫 You cannot chat with yourself." + Main.RESET);
            return;
        }
        User receiver = findUserByName(to);
        if (receiver == null) {
            System.out.println(Main.RED + "⚠️ User not found." + Main.RESET);
            return;
        }
        NormalChat chat = new NormalChat(user, receiver, storage);
        chat.displayMessagesFor(user);

        while (true) {
            String msg = input.getString("Message (type 'exit' to stop): ");
            if (msg.equalsIgnoreCase("exit"))
                break;
            if (msg.trim().isEmpty()) {
                System.out.println(Main.RED + "⚠️ Message cannot be empty." + Main.RESET);
                continue;
            }
            String timestamp = storage.getTimestamp();
            String full = "[" + timestamp + "] " + user.getUsername() + ": " + msg;
            storage.saveMessage(user.getUsername(), receiver.getUsername(), full);
        }
    }

    void groupFlow(User user) {
        boolean loop = true;
        while (loop) {
            System.out.println(Main.CYAN + "\n Group Options:" + Main.RESET + "\n1. Create Group"
                    + "\n2. Show Groups" + "\n3. View Group" + "\n4. Back");

            int choice = input.getChoice();

            switch (choice) {
                case 1:
                    createGroup(user);
                    break;
                case 2:
                    showGroups(user);
                    break;
                case 3:
                    viewGroup(user);
                    break;
                case 4:
                    loop = false;
                    System.out.println(Main.YELLOW + "⬅️ Returning to main menu..." + Main.RESET);
                    break;
                default:
                    System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
            }
        }
    }

    void createGroup(User user) {
        String gName = input.getString("Enter the Group Name: ");
        if (gName.isEmpty()) {
            System.out.println(Main.RED + "⚠️ Group name cannot be empty." + Main.RESET);
            return;
        }

        for (GroupChat g : GroupChat.allGroups)
            if (g.groupName.equalsIgnoreCase(gName)) {
                System.out.println(
                        Main.RED + "⚠️ A group with this name already exists. Choose another name." + Main.RESET);
                return;
            }

        String privateCheck = input.getString("🔒 Is the group private? (yes/no): ").toLowerCase();
        boolean isPrivate = false;
        switch (privateCheck) {
            case "yes":
            case "y":
                isPrivate = true;
                break;
            case "no":
            case "n":
                isPrivate = false;
                break;
            default:
                System.out.println(Main.RED + "⚠️ Invalid choice, So it is set to public" + Main.RESET);
        }
        System.out.println(Main.YELLOW + "set to private  ==> " + isPrivate + Main.RESET);

        GroupChat newGroup = new GroupChat(gName, user, isPrivate, storage);

        if (isPrivate) {
            ArrayList<User> candidates = new ArrayList<User>();
            for (User u : auth.getUsers())
                if (!u.getUsername().equalsIgnoreCase(user.getUsername()))
                    candidates.add(u);

            if (!candidates.isEmpty()) {
                System.out.println(Main.YELLOW + "\n----------- Available Users -----------" + Main.RESET);
                for (int i = 0; i < candidates.size(); i++)
                    System.out.println((i + 1) + ". " + candidates.get(i).getUsername());

                String line = input.getString("Enter user numbers to add (space separated): ");

                if (!line.isEmpty()) {
                    String[] parts = line.split("\\s+");
                    for (String p : parts) {
                        try {
                            int idx = Integer.parseInt(p);
                            if (idx < 1 || idx > candidates.size()) {
                                System.out.println(Main.RED + "⚠️ Invalid user number: " + p + Main.RESET);
                                continue;
                            }
                            User selected = candidates.get(idx - 1);
                            if (!newGroup.isMember(selected))
                                newGroup.members.add(selected);

                        } catch (NumberFormatException e) {
                            System.out.println(Main.RED + "⚠️ Invalid number: " + p + Main.RESET);
                        }
                    }
                }
            } else {
                System.out.println(Main.YELLOW + "\n----------- Available Users -----------" + Main.YELLOW
                        + "\n😕 No Users Available."
                        + Main.RESET);
            }
        }

        storage.saveGroups(GroupChat.allGroups);

        System.out.println(Main.GREEN + "✅ Group '" + gName + "' created successfully ("
                + (isPrivate ? "Private" : "Public") + ")." + Main.RESET);
    }

    void showGroups(User user) {
        System.out.println(Main.CYAN + "\n----------- Available Groups -----------" + Main.RESET);
        boolean found = false;
        for (GroupChat g : GroupChat.allGroups) {
            if (!g.isPrivate || g.isMember(user)
                    || (g.admin != null && g.admin.getUsername().equalsIgnoreCase(user.getUsername()))
                    || g.isSubAdmin(user)) {
                System.out.println("---> " + g.groupName + " (" + (g.isPrivate ? "Private" : "Public") + ")");
                found = true;
            }
        }
        if (!found)
            System.out.println(Main.YELLOW + "😕 No groups to show." + Main.RESET);
    }

    void viewGroup(User user) {
        String gName = input.getString("Enter the Group Name: ");
        GroupChat found = null;
        boolean groupDeleted = false, wantChat = true;
        for (GroupChat g : GroupChat.allGroups)
            if (g.groupName.equalsIgnoreCase(gName)) {
                found = g;
                break;
            }
        if (found == null) {
            System.out.println(Main.RED + "❌ No group found with that name." + Main.RESET);
            return;
        }

        if (found.isPrivate && !found.isMember(user) && !found.isSubAdmin(user)
                && !(found.admin != null && found.admin.getUsername().equalsIgnoreCase(user.getUsername()))) {
            String yORn = input.getString(Main.RED + "\n🚫 This is a Private group. Access denied." + Main.BLUE
                    + "\nIf you want to send a request to the group(yes|no): ").toLowerCase();
            switch (yORn) {
                case "yes":
                case "y":
                    found.addJoinRequest(user, storage);
                    storage.saveGroups(GroupChat.allGroups);
                    break;
                default:
                    System.out.println(Main.YELLOW + "❌ Request not sent." + Main.RESET);
            }
            return;
        }

        if ((found.admin != null && found.admin.getUsername().equalsIgnoreCase(user.getUsername()))
                || found.isSubAdmin(user)) {
            String isAadminOpen = input.getString("🔧 Open admin settings? (yes/no): ").toLowerCase();

            switch (isAadminOpen) {
                case "yes":
                case "y":
                    groupDeleted = found.adminSettings(user, auth);
                    break;
                case "no":
                case "n":
                    break;
                default:
                    System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
            }

            String wantToChat = input.getString("\n💭 Do you want to chat? (yes/no): ").toLowerCase();

            switch (wantToChat) {
                case "yes":
                case "y":
                    break;
                case "no":
                case "n":
                    wantChat = false;
                    break;
                default:
                    System.out.println(Main.RED + "⚠️ Invalid choice." + Main.RESET);
            }
        }

        if (!groupDeleted && wantChat) {
            System.out.println(Main.BLUE + "----------------- Message -----------------" + Main.RESET);
            found.displayMessages();
            while (true) {
                String msg = input.getString("Message (type 'exit' to go back): ");
                if (msg.equalsIgnoreCase("exit"))
                    break;
                if (msg.trim().isEmpty()) {
                    System.out.println(Main.RED + "⚠️ Message cannot be empty." + Main.RESET);
                    continue;
                }
                found.sendMessage(user, msg);
            }
        }
    }

    User findUserByName(String name) {
        for (User u : auth.getUsers())
            if (u.getUsername().equalsIgnoreCase(name))
                return u;
        return null;
    }
}
