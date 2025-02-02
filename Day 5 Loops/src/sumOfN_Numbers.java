import java.util.Scanner;

public class sumOfN_Numbers {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to program of Sum of N Numbers....");
        System.out.print("Enter the value of N: ");
        int n = sc.nextInt();

        int sum=0;
        int i=0;

        while (i<=n){
            sum= sum+i;
            i++;
        }

        System.out.println("The sum of N numbers is: "+sum);

    }
}
