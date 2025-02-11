public class linearSearch {
    public static void main(String[] args) {
        int numbers[] = {2,4,3,5,6,10,12,13,14};
        int  key = 11;
        int index = linearSearch(numbers,key);
        if(index != -1) {
            System.out.println("Key is present at the index of: " + linearSearch(numbers, key));
        }
        else{
            System.out.println("Not found");
        }
    }

    public static int linearSearch(int numbers[], int key){
        for(int i=0; i<numbers.length; i++){
            if (numbers[i]==key){
                return i;
            }
        }
        return -1;
    }
}
