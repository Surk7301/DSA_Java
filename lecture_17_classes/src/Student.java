public class Student {
    public  int id;
    public  int age;
    public String name;
    public int nos;

    // Default ctor
    public Student(){
        System.out.println("Student default ctor called");
    }

    public Student(int id, int age, String name, int nos){
        System.out.println("Inside parametrized constructor");
        this.id=id;
        this.name=name;
        this.age=age;
        this.nos=nos;
    }

    public Student(Student srcObj){
        System.out.println("Inside Copy constructor");
        this.id = srcObj.id;
        this.name = srcObj.name;
        this.age = srcObj.age;
        this.nos = srcObj.nos;
    }

    public void study(){
        System.out.println(name + " Studying");
    }

    public void sleep(){
        System.out.println(name + " Sleeping");
    }

    public void bunk(){
        System.out.println(name + " Bunking");
    }
}
