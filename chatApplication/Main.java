package chatApplication;

public class Main {
    public static String RESET = "\u001B[0m", RED = "\u001B[31m", GREEN = "\u001B[32m", YELLOW = "\u001B[33m",
            BLUE = "\u001B[34m", CYAN = "\u001B[36m", BOLD = "\u001B[1m";

    public static void main(String[] args) {
        System.out.println(CYAN + "\n╔════════════════════════════════════╗"
                + "\n║         WELCOME TO                 ║"
                + "\n║        ✨ CHAT APP ✨              ║"
                + "\n╠════════════════════════════════════╣"
                + "\n║   Connect, Chat & Have Fun! 🎉     ║"
                + "\n║   Sign Up or Login to Start 💻     ║"
                + "\n╚════════════════════════════════════╝" + RESET);

        FileStorage fileStorage = new FileStorage("UserDetails.txt");
        Authentication auth = new Authentication(fileStorage);

        auth.reloadUsersFromStorage();

        ChatApplication app = new ChatApplication(auth, fileStorage);
        app.loadGroupsAtStart();
        Input input = new Input();

        boolean running = true;
        final byte SIGN_UP = 1, LOGIN = 2, DISPLAY_USER = 3, EXIT = 4;
        while (running) {
            System.out.println(CYAN + "\n\n=====CHAT APP =====" + RESET + "\n1. Sign Up" + "\n2. Login"
                    + "\n3. Show Users" + "\n4. Exit");

            byte choice = input.getChoice();
            switch (choice) {
                case SIGN_UP:
                    new SignUp().signUp(auth, app);
                    break;
                    
                case LOGIN:
                    new Login().login(auth, app);
                    break;

                case DISPLAY_USER:
                    auth.displayPublicUsers();
                    break;

                case EXIT:
                    System.out.println(YELLOW + "👋 Goodbye!" + RESET);
                    running = false;
                    break;

                default:
                    System.out.println(RED + "⚠️ Invalid choice (1-4)." + RESET);
            }
        }
    }
}
