package chatApplication;

public class SignUp {
     public Input input = new Input();


     public boolean isValidPhone(String phone) {
          return phone != null && phone.matches("^(?:\\+91|0)?[6-9]\\d{9}$");
     }

     public void signUp(Authentication auth,ChatApplication app) {
          String uName, phone;
          while (true) {
               uName = input.getString("Enter username: ");
               if (auth.isUserExistsWithName(uName)) {
                    System.out.println(Main.RED + "⚠️ Username already exists" + Main.RESET);
                    continue;
               } else if (!uName.matches("[A-Za-z0-9_]{3,}")) {
                    System.out.println(Main.RED + "⚠️ Username atLeast be three letters" + Main.RESET);
                    continue;
               }
               break;
          }
          while (true) {
               phone = input.getString( "Enter phone Number: ");
               if (!isValidPhone(phone)) {
                    System.out.println(Main.RED + "⚠️ Incorrect Phone number." + Main.RESET);
                    continue;
               }
               if (auth.isUserExistsWithPhone(phone)) {
                    System.out.println(Main.RED + "⚠️ Phone number already exists." + Main.RESET);
                    continue;
               }
               break;
          }
          String pass = input.passWordCheck( "Enter password: ");
          String privateCheck = input.getString( "Is your account private? (yes/no): ").toLowerCase();

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

          User created = auth.signUp(uName, phone, pass, isPrivate);
          if (created != null) {
               System.out.println(Main.GREEN + "✅ Signed up and logged in as " + created.getUsername() + Main.RESET);
               app.startChatSession(created);
          }
     }
}
