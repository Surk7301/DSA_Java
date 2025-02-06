public class printAllPrimeInA_Range {
    public static void primesInRange(int n){

        for(int i=2; i<=n; i++){
            if(optimisidPrimeOrNot.isPrime(i)){
                System.out.print(i+" ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        primesInRange(100);
    }
}
