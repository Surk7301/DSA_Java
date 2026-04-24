public class removeSpaceFromSpace {
    public static void main(String[] args) {
        String str ="gef ihgewif hwqe h wiwr ih   hr";
        String updatedStr="";

        for(int i=0; i<str.length(); i++){
            char ch = str.charAt(i);
            if(ch == ' '){
                updatedStr=str.replace(" ","");
            }
        }
        System.out.println(updatedStr);
    }
}
