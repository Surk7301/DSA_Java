public class averageOfThreeNumbers {
    public static double average(double a, double b, double c){
        double avg =0;
        avg = (a+b+c)/3;
        return avg;

    }

    public static void main(String[] args) {
        System.out.println(average(2,3,5));
    }
}
