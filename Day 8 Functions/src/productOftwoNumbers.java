public class productOftwoNumbers {
    public static void main(String[] args) {
        int a =10;
        int b=5;
        int prod = multiply(a,b);

        System.out.println(prod);
    }

    private static int multiply(int a, int b) {
        int prod = a*b;
        return prod;
    }
}
