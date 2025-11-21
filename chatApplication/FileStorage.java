package chatApplication;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FileStorage extends Storage {
    String filename;
    String CHAT_DIR = "Chats/";
    static int keyToShift = 5;
    static String GROUPS_FILE = "GroupsList.txt";

    public FileStorage(String filename) {
        this.filename = filename;
        File base = new File(CHAT_DIR);
        if (!base.exists())
            base.mkdirs();
        File groupsDir = new File(CHAT_DIR + "Groups");
        if (!groupsDir.exists())
            groupsDir.mkdirs();
    }

    // ============ enc dec ============
    String encrypt(String text) {
        if (text == null)
            return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            int shift = 0;
            if (i % 3 == 0 && i % 5 == 0) {
                shift = 8;
            } else if (i % 3 == 0) {
                shift = 3;
            } else if (i % 5 == 0) {
                shift = 5;
            } else {
                shift = keyToShift;
            }

            sb.append((char) (c + shift));
        }

        return sb.toString();
    }

    String decrypt(String text) {
        if (text == null)
            return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            int shift = 0;
            if (i % 15 == 0) {
                shift = 8;
            } else if (i % 3 == 0) {
                shift = 3;
            } else if (i % 5 == 0) {
                shift = 5;
            } else {
                shift = keyToShift;
            }

            sb.append((char) (c - shift));
        }

        return sb.toString();
    }

    // ============ Users ============

    public void saveUser(User user) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filename, true));
            String line = user.userDetails();
            bw.write(encrypt(line));
            bw.newLine();
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Unable to save user: " + e.getMessage() + Main.RESET);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public ArrayList<User> getUser() {
        ArrayList<User> list = new ArrayList<User>();
        BufferedReader br = null;
        try {
            File file = new File(filename);
            if (!file.exists())
                return list;
            br = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = br.readLine()) != null) {
                String dec = decrypt(ln);
                User u = User.userDetailsByLine(dec);
                if (u != null)
                    list.add(u);
            }
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error reading users file: " + e.getMessage() + Main.RESET);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return list;
    }

    // ============ User dirs & group file create ============
    public File getUserDir(String username) {
        File dir = new File(CHAT_DIR + username);
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }

    public void createGroupFile(String groupName) {
        try {
            File dir = new File(CHAT_DIR + "Groups");
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, groupName + ".txt");
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error creating group file: " + e.getMessage() + Main.RESET);
        }
    }

    // ============ Personal messages ============
    public void saveMessage(String sender, String receiver, String fullMessageWithTimestamp) {
        BufferedWriter bw1 = null;
        BufferedWriter bw2 = null;
        try {
            File senderDir = getUserDir(sender);
            File receiverDir = getUserDir(receiver);
            File senderFile = new File(senderDir, receiver + ".txt");
            File receiverFile = new File(receiverDir, sender + ".txt");
            String encrypted = encrypt(fullMessageWithTimestamp);
            bw1 = new BufferedWriter(new FileWriter(senderFile, true));
            bw2 = new BufferedWriter(new FileWriter(receiverFile, true));
            bw1.write(encrypted);
            bw1.newLine();
            bw2.write(encrypted);
            bw2.newLine();
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error saving message: " + e.getMessage() + Main.RESET);
        } finally {
            try {
                if (bw1 != null)
                    bw1.close();
            } catch (IOException e) {
            }
            try {
                if (bw2 != null)
                    bw2.close();
            } catch (IOException e) {
            }
        }
    }

    public ArrayList<String> loadMessages(String username, String other) {
        ArrayList<String> out = new ArrayList<String>();
        BufferedReader br = null;
        try {
            File file = new File(CHAT_DIR + username + "/" + other + ".txt");
            if (!file.exists())
                return out;
            br = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = br.readLine()) != null)
                out.add(decrypt(ln));
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error loading messages: " + e.getMessage() + Main.RESET);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }
        return out;
    }

    // ============ Group messages ============
    public void saveGroupMessage(String groupName, String sender, String message) {
        BufferedWriter bw = null;
        try {
            File dir = new File(CHAT_DIR + "Groups");
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, groupName + ".txt");
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String full = "[" + timestamp + "] " + sender + ": " + message;
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(encrypt(full));
            bw.newLine();
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error saving group message: " + e.getMessage() + Main.RESET);
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                }
        }
    }

    public ArrayList<String> loadGroupMessages(String groupName) {
        ArrayList<String> out = new ArrayList<String>();
        BufferedReader br = null;
        try {
            File file = new File(CHAT_DIR + "Groups/" + groupName + ".txt");
            if (!file.exists())
                return out;
            br = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = br.readLine()) != null)
                out.add(decrypt(ln));
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error loading group messages: " + e.getMessage() + Main.RESET);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }
        return out;
    }

    // ============ GroupsList ============
    public void saveGroups(ArrayList<GroupChat> groups) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(GROUPS_FILE, false));
            if (groups == null)
                return;
            for (GroupChat g : groups) {
                String adminName = (g.admin != null) ? g.admin.getUsername() : "";
                String members = joinUsernames(g.members);
                String subs = joinUsernames(g.subAdmins);
                String line = g.groupName + "," + g.isPrivate + "," + adminName + "," + subs + "," + members;
                bw.write(encrypt(line));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error saving groups list: " + e.getMessage() + Main.RESET);
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                }
        }
    }

    public ArrayList<GroupChat> loadGroups(Authentication auth) {
        ArrayList<GroupChat> out = new ArrayList<GroupChat>();
        BufferedReader br = null;
        try {
            File file = new File(GROUPS_FILE);
            if (!file.exists())
                return out;
            br = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = br.readLine()) != null) {
                String dec = decrypt(ln);
                String[] p = dec.split(",", -1);
                if (p.length >= 5) {
                    String name = p[0];
                    boolean priv = Boolean.parseBoolean(p[1]);
                    String adminName = p[2];
                    String subs = p[3];
                    String mems = p[4];
                    User adminUser = auth.findUserByName(adminName);
                    if (adminUser == null)
                        continue;
                    GroupChat g = new GroupChat(name, adminUser, priv, this);
                    if (subs != null && !subs.trim().isEmpty()) {
                        String[] sarr = subs.split("\\|");
                        for (String s : sarr) {
                            User u = auth.findUserByName(s);
                            if (u != null && !g.subAdmins.contains(u))
                                g.subAdmins.add(u);
                        }
                    }
                    if (mems != null && !mems.trim().isEmpty()) {
                        String[] marr = mems.split("\\|");
                        for (String m : marr) {
                            User u = auth.findUserByName(m);
                            if (u != null && !g.members.contains(u))
                                g.members.add(u);
                        }
                    }
                    out.add(g);
                }
            }
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error reading groups list: " + e.getMessage() + Main.RESET);
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }
        return out;
    }

    public void deleteGroup(String groupName) {
        try {
            File chat = new File(CHAT_DIR + "Groups/" + groupName + ".txt");
            if (chat.exists())
                chat.delete();
        } catch (Exception e) {
            System.out.println(Main.RED + "⚠️ Error deleting group chat file: " + e.getMessage() + Main.RESET);
        }

        BufferedReader br = null;
        ArrayList<String> data = new ArrayList<>();
        try {
            File file = new File(GROUPS_FILE);
            if (!file.exists())
                return;
            br = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = decrypt(ln).split(",", -1);
                if (p.length >= 1) {
                    if (!p[0].equalsIgnoreCase(groupName))
                        data.add(ln);
                }
            }
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error updating groups list: " + e.getMessage() + Main.RESET);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(GROUPS_FILE, false));
            for (String l : data) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(Main.RED + "⚠️ Error saving groups list after delete: " + e.getMessage() + Main.RESET);
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                }
        }
    }

    String joinUsernames(ArrayList<User> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).getUsername());
            if (i < list.size() - 1)
                sb.append("|");
        }
        return sb.toString();
    }

    public String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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
