public class App {
    public static void main(String[] args) {
//        Student A = new Student();
//        A.name = "Suraj";
//        A.age = 26;
//        A.id = 001;
//        A.nos = 4;
//        System.out.println(A.name);
//        System.out.println(A.id);
//        System.out.println(A.age);
//        System.out.println(A.nos);
//
//        A.bunk();
//        A.study();
//        A.sleep();

        Student A = new Student(1, 26, "Suraj", 4);

        System.out.println(A.name);
        System.out.println(A.id);
        System.out.println(A.age);
        System.out.println(A.nos);

        A.bunk();
        A.study();
        A.sleep();

        Student B =  new Student(A);

        System.out.println(B.name);
        System.out.println(B.id);
        System.out.println(B.age);
        System.out.println(B.nos);

        B.bunk();
        B.study();
        B.sleep();


    }
}
