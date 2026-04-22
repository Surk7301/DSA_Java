import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
     int[][] arr;
     arr = new int[3][4]; // declaration and initialization of 2D array
      int brr[][]={
              {1,2},
              {3,4},
              {5,6},
              {7,8}
      };
//        System.out.println(brr[3][0]);
//        int rowLength = brr.length;
//        int colLength = brr[0].length;
//        for(int rowIndex=0; rowIndex<=rowLength-1; rowIndex++){
//            for(int colIndex=0; colIndex<=colLength-1; colIndex++){
//                System.out.print(brr[rowIndex][colIndex]+" ");
//            }
//            System.out.println();
//        }


//        int brr1[][]={
//                {1,2},
//                {3,4,5},
//                {6,7,8,9}
//        };
//        int rowLength = brr1.length;
//        for(int rowIndex=0; rowIndex<=rowLength-1;  rowIndex++ ){
//            int colLength = brr1[rowIndex].length;
//            for (int colIndex =0; colIndex<=colLength-1; colIndex++){
//                System.out.print(brr1[rowIndex][colIndex]+" ");
//            }
//            System.out.println();
//        }

        //  traverse 2D array using for
//        for(int rowIndex=0; rowIndex<=brr.length-1; rowIndex++){
//            for(int colIndex=0; colIndex<=brr[rowIndex].length; colIndex++){
//                System.out.println(brr[rowIndex][colIndex] + " ");
//            }
//        }

//        int brr2[][]= new int[3][4];
//        int sum=0;
//        Scanner sc = new Scanner(System.in);
//        // Taking input
//        for(int i=0; i<=arr.length-1; i++){
//            for(int j=0; j<=arr[i].length-1; j++){
//                System.out.println("Enter value for index " + i + " " + j);
//                brr2[i][j] = sc.nextInt();
//            }
//        }
//
//        for(int i=0; i<= brr2.length-1; i++){
//
//            for(int j=0; j<=brr2[i].length-1; j++){
//                int value  = brr2[i][j];
//                System.out.print(brr2[i][j] + " ");
//                sum += value;
//            }
//
//            System.out.println();
//        }
//        System.out.println("Sum: "+sum);

        //Multiply of 2D array elements
//        int brr3[][] = {
//                {1,2,3},
//                {4,5,6},
//                {7,8,9}
//        };
//        int multiply = 1;
//        for(int i=0; i<=brr3.length-1;  i++) {
//            for(int j=0; j<=brr3[i].length-1; j++){
//                int value = brr3[i][j];
//                System.out.print(brr3[i][j] + " ");
//                multiply *= value;
//
//            }
//            System.out.println();
//        }
//        System.out.println("Multiply: " + multiply);

        // Maximum element in 2D array
        int brr4[][] = {
                {1,2,3},
                {4,5,6},
                {7,8,9}
        };
        int maxValue = brr4[0][0];
        for(int i=0; i<=brr4.length-1; i++){
            for (int j=0; j<=brr4[i].length-1; j++){
                if(brr4[i][j]>maxValue){
                    maxValue = brr4[i][j];
                }
            }
        }
        System.out.println("Maximum value in 2D array is: " + maxValue);

    }
}