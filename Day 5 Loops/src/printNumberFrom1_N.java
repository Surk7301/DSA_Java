import java.util.Scanner;

public class printNumberFrom1_N {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number till you want to print: ");
        int n = sc.nextInt();
        int i =1;

        while (i<=n){
            System.out.println(i);
            i++;
        }

    }
}
