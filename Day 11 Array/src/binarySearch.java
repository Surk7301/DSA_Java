public class binarySearch {

    public static int binarySearch(int numbers[], int key){
        int start =0, end = numbers.length-1;

        while(start <= end){
            int mid = (start+end)/2;

            if(numbers[mid] == key){
                return mid;
            }
            if(numbers[mid]< key){
                start = mid+1;
            }
            else{
                end = mid-1;
            }


        }
        return -1;

    }
    public static void main(String[] args) {
        int numbers[] ={1,4,5,7,56,345,555,675,786,875,988};
        int key = 988;
        System.out.println("Index for key is: "+ binarySearch(numbers,key));
    }


}
