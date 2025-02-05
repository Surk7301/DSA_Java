public class binomialCoefficient {
    public static void main(String[] args) {
        System.out.println(binCoeff(5,2));

    }
    public  static int binCoeff(int n, int r){
        int fact_n = factorialOfA_Number.factorial(n);
        int fact_r = factorialOfA_Number.factorial(r);
        int fact_nmr = factorialOfA_Number.factorial(n-r);

        int binCoeff = fact_n/(fact_r*fact_nmr);
        return binCoeff;

    }
}
