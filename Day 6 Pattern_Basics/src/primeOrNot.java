import java.util.Scanner;

public class primeOrNot {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number you want to print: ");
        int n= sc.nextInt();

        boolean isPrime= true;
        for(int i=2; i<=n-1; i++){
            if(n%i ==0){
                isPrime=false;
            }
        }
        if(isPrime==true){
            System.out.println(n + " is a prime number.");
        }
        else{
            System.out.println(n + " is not a prime number.");
        }
    }
}
