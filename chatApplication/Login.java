package chatApplication;

public class Login {
     Input input = new Input();

     public void login(Authentication auth, ChatApplication app) {
          String uNameLogin = input.getString("Enter username: ");
          String passLogin = input.passWordCheck("Enter password: ");
          User logged = auth.login(uNameLogin, passLogin);
          if (logged != null) {
               app.startChatSession(logged);
          }
     }
}
