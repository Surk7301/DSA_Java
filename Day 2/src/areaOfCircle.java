import java.util.Scanner;

public class areaOfCircle {
    public static void main(String[] args) {
        System.out.println("Welcome to Area of a Circle program 😊");

        Scanner input = new Scanner(System.in);
        float pie = 3.14F;
        float area =0;

        System.out.print("Enter the value of radius: ");
        float radius = input.nextFloat();

        area = (pie * (radius*radius));
        System.out.println("The area of circle is: "+area);

    }
}
