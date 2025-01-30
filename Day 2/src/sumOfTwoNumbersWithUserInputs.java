import java.util.Scanner;

public class sumOfTwoNumbersWithUserInputs {
    public static void main(String[] args) {
        System.out.println("Welcome to sum of two numbers program 😊");

        int a =10;
        int b=10;
        System.out.println("Sum of a and b is: " + (a+b));

//        Sum of two numbers from user input
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the first number: ");
        int c = input.nextInt();
        System.out.print("Enter the second number: ");
        int d = input.nextInt();
        System.out.println("The Sum of first and second number is: "+(c+d));

    }
}
