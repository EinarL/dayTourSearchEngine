package vinnsla;

/**
 * Klasi fyrir Participant
 */
public class Participant {

    private String name;
    private String ss; //kennitala
    private String pn; //símanúmer
    private int age;


    public Participant(String name, String ss, String pn, int age){
        this.name = name;
        this.ss = ss;
        this.pn = pn;
        this.age = age;
    }

    public String getName() { return name; }

    public String getSs() { return ss; }

    public String getPn() { return pn; }

    public int getAge() { return age; }

}
