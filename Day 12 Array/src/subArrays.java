public class subArrays {

    public static void printSubarrays(int numbers[]){
        int tp=0;
        for(int i=0; i<numbers.length; i++){
            int start = i;
            for(int j=0; j<numbers.length; j++){
                int end = j;
                for(int k=start; k<=end; k++){
                    System.out.print(numbers[k]+ " ");
                    tp++;
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("Count of subAraay is: "+tp);
    }

    public static void main(String[] args) {
        int numbers[]={2,4,5,6,7,8,10};
        printSubarrays(numbers);
    }
}
