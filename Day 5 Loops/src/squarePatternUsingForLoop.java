import java.util.Scanner;

public class squarePatternUsingForLoop {
    public static void main(String[] args) {
        System.out.println("Welcome to Square pattern program....");
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the numbers how many stars you need in a row: ");
        int n = sc.nextInt();

        for (int i =0; i<n; i++){
            for (int j=0; j<n; j++){
                System.out.print("* ");
            }
            System.out.println();
        }

    }
}
