public class loop {
    public static void main(String[] args) {
//        for(int i=1; i<=5; i++){
//            System.out.println("Value of i: "+i);
//        }

//        for(int i=1; i<12; i++){
//            System.out.println("Hello");
//        }

//        for(int i=1; i<=10; i +=2){
//            System.out.println("Value of i: "+i);
//        }

        //Nested loops
        for(int i=0; i<=3; i++){
            for (int j=0; j<3; j++){
                System.out.print("* ");
            }
            System.out.println();
        }

        for(int i=1; i<=3; i++){
            for(j=1; j<=3; j++){
                System.out.println("i= "+ i +"j = "+ j);
            }
        }
    }
}
