import java.util.Scanner;

public class taxCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user input for annual income
        System.out.print("Enter your annual income: ");
        double income = scanner.nextDouble();
        double tax = 0;

        // Tax calculation as per the old tax regime (simplified for example)
        if (income <= 250000) {
            tax = 0;
        } else if (income <= 500000) {
            tax = 0.05 * (income - 250000);
        } else if (income <= 1000000) {
            tax = 0.05 * 250000 + 0.2 * (income - 500000);
        } else {
            tax = 0.05 * 250000 + 0.2 * 500000 + 0.3 * (income - 1000000);
        }

        System.out.println("Your calculated tax is: Rs. " + tax);
        scanner.close();
    }
}
