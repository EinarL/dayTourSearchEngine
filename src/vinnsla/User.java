package vinnsla;

/**
 * Klasi sem inniheldur upplýsingar um hvaða notandi er signaður inn.
 */
public class User {

    private static String user = null;

    public static String getUsername(){
        return user;
    }

    public static void setUsername(String username){
        user = username;
    }
}
