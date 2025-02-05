public class factorialOfA_Number {
    public static void main(String[] args) {
        int input = 10;

        int output = factorial(input);
        System.out.println(output);
    }

    public static int factorial(int input){
        int fact =1;
        for(int i=1; i<=input; i++){
            fact=fact*i;


        }
        return fact;

    }
}
