//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static  void printDigits(int num){
        while(num != 0){
            int digit = num%10;
            System.out.println(digit);
            num=num/10;
        }
    }
    public static void main(String[] args) {
        int num = 454564;
        printDigits(num);


    }
}