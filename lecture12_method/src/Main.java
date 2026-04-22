//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void print2katable(){
        for(int i=1; i<=10; i++){
            System.out.println("2 x " + i + " = " + 2*i);
        }
    }

    static void printSum(int a, int b){
        System.out.println("Sum is: " + (a+b));
    }
    static  void printSum(int a, int b, int c){
        System.out.println("Sum is: " + (a+b+c));
    }

    static int printMultiply(int a , int b){
//        System.out.println("Multiply is: " + (a*b));
        int multiply = a * b;
        System.out.println("Multiply is: " + multiply);

        return multiply;
    }
    public static void main(String[] args) {
//    print2katable();

        printSum(2, 3, 5);

        printMultiply(4,5);
    }
}