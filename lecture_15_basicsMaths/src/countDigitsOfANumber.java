public class countDigitsOfANumber {
    static  void countDigits(int num){
        int count =0;
        while(num !=0){
            int digit = num%10;
            count++;
            num = num/10;

        }
        System.out.println(count);
    }
    public static void main(String[] args) {
        int num = 863836;
        countDigits(num);

    }
}
