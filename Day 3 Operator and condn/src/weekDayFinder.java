import java.util.Scanner;

public class weekDayFinder {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user input for week number
        System.out.print("Enter a week number (1-7): ");
        int weekNumber = scanner.nextInt();

        String day;
        switch (weekNumber) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
            default:
                day = "Invalid week number!";
        }

        System.out.println("The day of the week is: " + day);
        scanner.close();
    }
}
