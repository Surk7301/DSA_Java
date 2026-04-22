//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        String firstName = "Suraj";
//        String lastName = new String("Kumar");

//        System.out.println(firstName+ " " + lastName);
//        System.out.println(firstName[0]);
//        System.out.println(firstName.length());
//        System.out.println(firstName.charAt(4));

        String name1 = "Love";
        String name2 = "Love";

        if (name1 == name2) { // compare reference of string
            System.out.println("Both strings are same");
        } else {
            System.out.println("Both strings are different");

        }

        if (name1.equals(name2)) { // compare content of string
            System.out.println("Both strings are same");
        } else {
            System.out.println("Both strings are different");

        }

        if (name1.equalsIgnoreCase(name2)) { // ignore case sensitivity
            System.out.println("Both strings are same");
        } else {
            System.out.println("Both strings are different");

        }
    }
}