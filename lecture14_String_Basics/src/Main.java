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

//        String name1 = "Love";
//        String name2 = "Love";
//
//        if (name1 == name2) { // compare reference of string
//            System.out.println("Both strings are same");
//        } else {
//            System.out.println("Both strings are different");
//
//        }
//
//        if (name1.equals(name2)) { // compare content of string
//            System.out.println("Both strings are same");
//        } else {
//            System.out.println("Both strings are different");
//
//        }
//
//        if (name1.equalsIgnoreCase(name2)) { // ignore case sensitivity
//            System.out.println("Both strings are same");
//        } else {
//            System.out.println("Both strings are different");
//
//        }

        String str = "    SURAJ    ";
        System.out.println(str.length());
        System.out.println(str.charAt(0));
        String str1 = "suraj";
        String input = "My,name,is,suraj";
        String[] words = input.split(",");
        System.out.println(str.equals(str1));
        System.out.println(str.equalsIgnoreCase(str1));
        System.out.println(str.isBlank());
        System.out.println(str.isEmpty());
        System.out.println(str.trim());
        String trimmed = str.trim();
        System.out.println(trimmed.length());
        System.out.println(str1.toUpperCase());
        System.out.println(str.trim().toLowerCase());
        System.out.println(str.trim().substring(2,4));
        System.out.println(str1.contains("suraj"));
        System.out.println(str1.startsWith("su"));
        System.out.println(str1.endsWith("aj"));

        char[] ch = str1.toCharArray();

        for(char ch1: ch){
            System.out.println(ch1);
        }

        for (String word: words){
            System.out.println(word);
        }

        System.out.println(str1.replace('u', 'S'));



    }
}