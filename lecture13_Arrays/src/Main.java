import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
    int arr[];

    arr = new int[5]; // declaration and initialization of array
    int brr[] = {10,20,30,40,50}; // declaration and initialization of array in one line

//        System.out.println("Value at 0 index: " +brr[0]);
//        System.out.println("Value at 0 index: " +brr[1]);
//        System.out.println("Value at 0 index: " +brr[2]);
//        System.out.println("Value at 0 index: " +brr[3]);

        int n = brr.length; // length of array
//        int sum =0 ;
//        for(int i =0; i<=n-1; i++){
//            int value = brr[i];
//            sum += value;
//
//        }
//        System.out.println("Sum of array is: " + sum);

//        int multiply =1;
//        for(int i=0; i<=n-1; i++){
//            int value = brr[i];
//            multiply *= value;
//        }
//        System.out.println("Multiply of array is: " + multiply);

        // Maximum element in array

//        for(int val: brr){
//            System.out.println(val);
//        }

//        int arr2[]= new int[5];

//        for(int i=0; i<arr2.length; i++){
//            System.out.println("Enter value for index " + i);
//              arr2[i] = scan.nextInt();
//        }
//        for(int val1: arr2){
//            System.out.println("Array contains: ");
//            System.out.println(val1);
//        }

        int arrr[] = {3,2,-5,21,10};
        int size = arr.length;
        int maxValue = arrr[0];
        for(int i=0; i<=n-1; i++){
            if(arrr[i]>maxValue){
                maxValue = arrr[i];
            }
        }
        System.out.println("Maximum value in array is: " + maxValue);

        int minValue = arrr[0];
        for(int i=0;i<n-1; n++){
            if(arrr[i]<minValue){
                minValue=arrr[i];
            }
        }
        System.out.println("Minimum value in array is: " + minValue);


    }
}