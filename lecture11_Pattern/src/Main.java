//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        // Solid rectangle patern
//        for(int row=1; row<=4; row++){
//            for(int col=1; col<=4; col++){
//                System.out.print("* ");
//            }
//            System.out.println();
//        }
         //Pattern2
//        for(int row=1; row<=4; row++){
//            for(int col=1; col<=5; col++){
//                System.out.print("* ");
//            }
//            System.out.println();
//        }

            //Pattern3: Triangular pattern
//        for(int row=1; row<=5; row++){
//            for each row -> variable columns
//            formula -> columns -> 1-> value of row
//            for(int col=1; col<=row; col++){
//                System.out.print("* ");
//            }
//            System.out.println();
//        }

        //Pattern4: rhombus pattern
//        int n=5;
//        for(int row=1; row<=n; row++){
//            for(int col=1; col<=n-row; col++){
//                System.out.print( " ");
//            }
//
//            for(int col=1; col<=n; col++){
//                System.out.print("* ");
//            }
//
//            System.out.println();
//        }

        //Pattern5: Inverted Triangular pattern
//        int n=5;
//        for(int row=1; row<=n; row++){
//            for(int col=1; col<=n-row+1; col++){
//                System.out.print("* ");
//            }
//            System.out.println();
//        }

        //Pattern6: Pyramid pattern
//        int n=5;
//        for(int row=1; row<=n; row++){
//
//            for(int col=1; col<=n-row; col++){
//                System.out.print("  ");
//            }
//            for(int col=1; col<=(2*row)-1; col++){
//                System.out.print("* ");
//            }
//            System.out.println();
//        }

        //Pattern7:
        int n=4;
        for (int row=1; row<=n; row++){
            //spaces
            for(int col=1; col<=row-1; col++){
                System.out.print("  ");
            }
            //
            for(int col=1; col<=2*n-2*row+1; col++){
                System.out.print("* ");
            }
            System.out.println();
        }






    }
}