package vinnsla;

/**
 * Klasi sem inniheldur upplýsingar um hvaða notandi er signaður inn.
 */
public class User {

    private static String user = null;
    private static int userID = -1;

    public static String getUsername(){
        return user;
    }

    public static int getUserID(){
        return userID;
    }

    public static void setUsername(String username){
        user = username;
    }

    public static void setUserID(int id){
        userID = id;
    }
}
