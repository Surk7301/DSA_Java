import java.util.Scanner;

public class averageOfThreeNumbers {
    public static void main(String[] args) {
        System.out.println("Welcome to Average of three numbers 😊");
        Scanner input = new Scanner(System.in);
        float average=0;

        System.out.print("Enter first number: ");
        int num1 = input.nextInt();

        System.out.print("Enter first number: ");
        int num2 = input.nextInt();

        System.out.print("Enter first number: ");
        int num3 = input.nextInt();

        average = (num1+num2+num3)/3;



        System.out.println(average);
    }
}
