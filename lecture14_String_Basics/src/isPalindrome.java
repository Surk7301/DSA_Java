public class isPalindrome {
    public static void main(String[] args) {
        String name = "NOON";
        int n = name.length();
        String reverse = "";

        for(int i=n-1; i>=0; i--){
            char ch  = name.charAt(i);
            reverse = reverse + ch;
        }
        System.out.println(name + " "+ reverse);
        if(name.equals(reverse) ){
            System.out.println("It's a palindrome");
        }else
            System.out.println("Not a Palindrome");
    }
}
