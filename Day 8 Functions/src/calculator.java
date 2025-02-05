public class calculator {
    public static void main(String[] args) {
        System.out.println(sum(3,5));
        System.out.println(sum(3,5,6));
        System.out.println(mul(3,5,6));
        System.out.println(mul(3,5));
        System.out.println(sum(3.5f,5.6f));

    }
    public static int sum(int a, int b){
        return a+b;
    }
    public static float sum(float a, float b){
        return a+b;
    }
    public static int sum(int a, int b, int c){
        return a+b+c;
    } public static int mul(int a, int b){
        return a*b;
    }
    public static int mul (int a, int b, int c){
        return a*b*c;
    }
}
