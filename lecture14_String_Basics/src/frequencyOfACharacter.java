public class frequencyOfACharacter {
    public static void main(String[] args) {
        String str = "jvbsdifh3jfknowjf";
        int count=0;
        for(int i=0; i<str.length(); i++){
            char ch = str.charAt(i);
            if(ch == 'i'){
                count++;
            }


        }
        System.out.println("Total Count: " + count);
    }
}
