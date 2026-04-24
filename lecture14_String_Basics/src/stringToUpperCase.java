public class stringToUpperCase {
    public static void main(String[] args) {
        String str = "dsgiherighkdns";
        String uppercase ="";

        for(int i=0; i<str.length(); i++){
            char ch = str.toUpperCase().charAt(i);
            uppercase = uppercase+ch;
        }
        System.out.println(uppercase);


    }
}
