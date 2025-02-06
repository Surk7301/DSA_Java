import java.util.Scanner;

public class primeOrNot {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();

        System.out.println(isPrime(n));

    }

    public static boolean isPrime(int n){
//        corner cases
//        2
        if(n==2){
            return true;
        }

        for(int i=2; i<=n-1; i++){
            if(n%i==0){
                 return false;
            }

        }
        return true;

    }
}
