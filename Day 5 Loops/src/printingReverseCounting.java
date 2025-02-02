import java.util.Scanner;

public class printingReverseCounting {
    public static void main(String[] args) {

        int num =100;
        for(int i=num; i>=1; i-- ){
            System.out.println(i);
        }


//        By taking input from user
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number from where you want to start reverse counting: ");
        int n = sc.nextInt();

        for(int i=n; i>=1; i--){
            System.out.println(i);
        }


    }
}
