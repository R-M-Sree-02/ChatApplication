package chatApplication;

import java.io.Console;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Input {
     public Scanner input = new Scanner(System.in);

     public String getString(String toAsk) {
          while (true) {
               System.out.print(Main.BLUE + toAsk + Main.RESET);
               String s = input.nextLine().trim();
               if (s.isEmpty()) {
                    System.out.println(Main.RED + "⚠️ Input cannot be empty." + Main.RESET);
                    continue;
               }
               return s;
          }
     }

     public String passWordCheck(String toAsk) {
          Console console = System.console();
          while (true) {
               String password = new String(console.readPassword(Main.BLUE + toAsk + Main.RESET)).trim();
               if (password.isEmpty()) {
                    System.out.println(Main.RED + "⚠️ Input cannot be empty." + Main.RESET);
                    continue;
               }
               return password;
          }
     }

     public byte getChoice() {

          byte choice = -1;

          while (true) {
               try {
                    System.out.print(Main.BLUE + "👉 Enter choice: " + Main.RESET);
                    choice = input.nextByte();
                    break;
               } catch (InputMismatchException e) {
                    System.out.println(Main.RED + "Enter a valid Number." + Main.RESET);
               } catch (Exception e) {
                    System.out.println(Main.RED + "Invalid Number." + Main.RESET);
               }
          }
          input.nextLine();
          return choice;
     }
}
