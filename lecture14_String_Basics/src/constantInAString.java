public class constantInAString {
    public static void main(String[] args) {
        String Str = "dseheghjyjof";
        int count=0;
        for(int i=0; i<Str.length(); i++){
            char ch = Str.charAt(i);
            if(ch !='a' && ch !='e' && ch !='i'&& ch !='o'&& ch!='u'){
                count++;
            }
        }
        System.out.println("Total constants: " + count);
    }
}
