package vinnsla;

import com.sun.marlin.FloatMath;
import javafx.scene.image.Image;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.awt.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DayTourRepository {

    private static Connection conn = null;

    /**
     * Fær tengingu við databaseinn ef það er ekki núþegar tenging.
     * @throws ClassNotFoundException
     */
    private static void getConnection() throws ClassNotFoundException {
        if(conn != null) return;
        Class.forName("org.sqlite.JDBC");

        try{
            conn = DriverManager.getConnection("jdbc:sqlite:src/database/dayTourSearchEngine.db");
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }

    }

    private static DayTour makeDayTour(ResultSet r) throws Exception {
        String[] imgs = r.getString("images").split(",");
        Image[] images = new Image[imgs.length];
        for(int i = 0; i < images.length; i++){
            if(!imgs[i].equals("")){
                images[i] = new Image(imgs[i], 588, 444, false, false); // breidd og hæð á myndinni
            }
        }
        float rating = r.getFloat("Rating");
        if(r.getInt("amountOfRatings") == 0){
            // ef enginn hefur gefið þessari ferð einkunn þá er þessi dagsferð ekki með rating
            // við táknum það með -1
            rating = -1;
        }
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(r.getString("Date"));
        DayTour dt = new DayTour(r.getInt("ID"),r.getString("Title"), r.getString("Description"), images, r.getString("images"), date,
                r.getInt("Price"), r.getInt("MaxSpots"), r.getInt("spotsLeft"), r.getString("Location"),
                r.getInt("Duration"), rating);

        return dt;
    }

    /**
     * Þessi aðferð skilar dagsferðum sem tilheyra landssvæðinu 'area'
     * og hafa titil sem er líkt og 'search'. Þeim dagsferðum eru röðuð eftir 'order'.
     * @param area fá aðeins þá sem tilheyra þessu landssvæði,
     *             getur verið: "Allt land","Vesturland","Norðurland","Suðurland" eða "Austurland",
     *             ef það er "Allt land", þá skilar aðferðin öllum dagsferðum.
     * @param search leitarstrengur fyrir Title í dagsferð.
     * @param order Strengur sem segir til um hvort á að raða eftir Einkunn eða Stafrófsröð eða Verði.
     * @return skilar lista af dagsferðum.
     */
    public static DayTour[] getDayTours(String area, String search, String order) throws Exception{
        getConnection();

        String orderBy = switch (order) {
            case "Einkunn" -> "Rating DESC";
            case "Stafrófsröð" -> "Title";
            case "Verð" -> "Price";
            default -> null;
        };

        Statement s = conn.createStatement();
        String query = "SELECT * FROM dayTours WHERE Title LIKE '%" + search +  "%' AND Location = '" + area + "'";
        if (area.equals("Allt land")){
            query = "SELECT * FROM dayTours WHERE Title LIKE '%" + search + "%'";
        }
        if (orderBy != null){
            query += " ORDER BY " + orderBy;
        }

        ResultSet res = s.executeQuery(query);
        ArrayList<DayTour> dayTourArray = new ArrayList<>();
        while(res.next()){
            dayTourArray.add(makeDayTour(res));
        }
        return dayTourArray.toArray(DayTour[]::new);
    }

    /**
     * fall til þess að reikna ratings hjá öllum dagsferðum.
     * þetta fall þarf að keyrast ef það er bætt við dagsferð og rating fyrir sá dagsferð í gegnum databaseið
     * til þess að rating sé rétt á dagsferðinni, ef það er bætt við dagsferð í gegnum forritið, þá þarf ekki
     * að keyra þetta fall.
     * Rating = sumOfRatings / amountOfRatings
     * @throws Exception
     */

    public static void refreshRatings() throws Exception {
        getConnection();
        // byrjum á að setja sum og amount of ratings sem 0
        Statement s = conn.createStatement();
        String sql = "UPDATE dayTours SET sumOfRatings = 0, amountOfRatings = 0";
        s.executeUpdate(sql);

        // fáum öll ratings
        sql = "SELECT rating, dayTourID from comments WHERE rating IS NOT NULL";
        ResultSet res = s.executeQuery(sql);

        // bætum við ratings í dayTours töfluna á viðeigandi dagsferð
        while(res.next()){
            addRating(res.getInt("dayTourID"), res.getFloat("rating"));
        }
    }

    public static void addDayTour(String title, String description, String images, String date, int price, int seats,
                                  String location, int duration) throws Exception {
        getConnection();

        String sql = "INSERT INTO dayTours(Title, Owner, Description, Images, Date, Price, MaxSpots, SpotsLeft, Location, Duration) VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, title);
        pstmt.setString(2, User.getUsername());
        pstmt.setString(3, description);
        pstmt.setString(4, images);
        pstmt.setString(5, date);
        pstmt.setInt(6, price);
        pstmt.setInt(7, seats);
        pstmt.setInt(8, seats);
        pstmt.setString(9, location);
        pstmt.setInt(10, duration);
        pstmt.executeUpdate();
    }

    /**
     *  Uppfærir dagsferðina dtTitleToEdit með nýjum upplýsingum
     * @param dtTitleToEdit
     * @param newTitle
     * @param desc
     * @param images
     * @param date
     * @param price
     * @param spots
     * @param location
     * @param duration
     * @throws Exception
     */
    public static void editDayTour(String dtTitleToEdit, String newTitle, String desc, String images,
                                   String date, int price, int spots, int spotsTaken, String location, int duration) throws Exception {
        String sql = "UPDATE dayTours SET Title = ?, Description = ?, Images = ?, Date = ?, Price = ?," +
                                        " MaxSpots = ?, SpotsLeft = ? - ?, Location = ?, Duration = ? WHERE Title = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newTitle);
        pstmt.setString(2, desc);
        pstmt.setString(3, images);
        pstmt.setString(4, date);
        pstmt.setInt(5, price);
        pstmt.setInt(6, spots);
        pstmt.setInt(7, spots);
        pstmt.setInt(8, spotsTaken);
        pstmt.setString(9, location);
        pstmt.setInt(10, duration);
        pstmt.setString(11, dtTitleToEdit);
        pstmt.executeUpdate();
    }


    public static void addBooking(String user, int numParticiapants, String tourName, int id, int price){
        try{
            getConnection();

            String sql = "UPDATE dayTours SET SpotsLeft = SpotsLeft - " + numParticiapants + " WHERE title = '" + tourName + "'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            sql = "INSERT INTO bookings VALUES(?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setInt(2, id);
            pstmt.setInt(3, numParticiapants);
            pstmt.setInt(4, price);
            pstmt.executeUpdate();


        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void removeBooking(String user, int dayTourId, String tourName){
        try{
            getConnection();
            Statement s = conn.createStatement();

            String query = "SELECT numParticipants FROM bookings WHERE username = '" + user + "' AND dayTourID = " + dayTourId;
            ResultSet rs = s.executeQuery(query);
            int numParticipants = rs.getInt(1);

            String sql = "UPDATE dayTours SET spotsLeft = spotsLeft + " + numParticipants + " WHERE title = '" + tourName + "'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            sql = "DELETE FROM bookings WHERE username = '" + user + "' AND dayTourID = " + dayTourId + " AND numParticipants = " + numParticipants;
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Fall til þess að fá number of participants og price frá booking töflunni.
     * @return int [num of participants, price]
     */
    public static int[] getBooking(int dayTourID){
        try{
            Statement s = conn.createStatement();
            String query = "SELECT numParticipants, price from bookings WHERE username = '" + User.getUsername() + "'" +
                    " AND dayTourID = " + dayTourID;
            ResultSet res = s.executeQuery(query);

            return new int[]{res.getInt("numParticipants"), res.getInt("price")};
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static DayTour[] getDayToursByUserBooked(String user) {

        ArrayList<DayTour> dayTourArray = null;
        try {
            getConnection();

            Statement s = conn.createStatement();
            String query = "SELECT dayTourID from bookings WHERE username ='" + user + "'";
            ResultSet rs = s.executeQuery(query);

            dayTourArray = new ArrayList<>();
            while (rs.next()) {
                Statement s2 = conn.createStatement();
                query = "SELECT * from dayTours WHERE ID=" + rs.getInt(1);
                ResultSet res = s2.executeQuery(query);
                while (res.next()){
                    dayTourArray.add(makeDayTour(res));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayTourArray.toArray(DayTour[]::new);
    }

    public static DayTour[] getDayToursByOwner(String owner) throws Exception {

        Statement s = conn.createStatement();

        String query = "SELECT * FROM dayTours WHERE Owner = '" + owner+ "'";
        ResultSet res = s.executeQuery(query);
        ArrayList<DayTour> dayTourArray = new ArrayList<>();
        while(res.next()){
            dayTourArray.add(makeDayTour(res));
        }
        return dayTourArray.toArray(DayTour[]::new);
    }

    public static DayTour getDayTourByTitle(String title) throws Exception {
        getConnection();
        Statement s = conn.createStatement();
        String query = "SELECT * from dayTours WHERE title = '" + title + "'";
        ResultSet res = s.executeQuery(query);

        return makeDayTour(res);
    }

    public static boolean hasUserBookedDayTour(int tourID) throws Exception {
        getConnection();
        Statement s = conn.createStatement();

        String query = "SELECT * from bookings WHERE username = '" + User.getUsername() + "' AND dayTourID = '" + tourID + "'";
        ResultSet res = s.executeQuery(query);

        return res.next();
    }

    public static boolean hasUserRatedDayTour(int dayTourID) throws Exception {
        getConnection();
        Statement s = conn.createStatement();

        String query = "SELECT * from comments WHERE userCommented = '" + User.getUsername() + "' AND dayTourID = '" + dayTourID + "' " +
                "AND rating IS NOT NULL";
        ResultSet res = s.executeQuery(query);
        // skilar true ef user hefur gefið dagsferðinni einkunn, annars false því resultset mun vera tómt
        return res.next();
    }

    public static Comment[] getCommentsByTour(int tourID, String order) throws Exception{
        getConnection();
        Statement s = conn.createStatement();

        String orderBy = "";
        if(order.equals("Likes")){
            orderBy = "ORDER BY Likes DESC";
        }
        String query = "SELECT * from comments WHERE dayTourID = '" + tourID + "'" + orderBy;

        ResultSet res = s.executeQuery(query);
        ArrayList<Comment> commentArray = new ArrayList<>();
        while(res.next()){
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("date"));

            float rating = res.getFloat("rating");
            if(res.wasNull()){ // ef að rating er NULL, setjum það sem -1
                rating = -1;
            }
            Comment comment = new Comment(res.getInt("ID"), res.getString("userCommented"),
                    res.getInt("dayTourID"), res.getString("commentText"), res.getInt("likes"), date, rating);
            commentArray.add(comment);
        }

        return commentArray.toArray(Comment[]::new);
    }

    public static void addComment(int dayTourID, String comment, float rating) throws Exception {
        getConnection();
        String sql = "INSERT INTO comments(dayTourID, userCommented, commentText, date, likes, rating) VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, dayTourID);
        pstmt.setString(2, User.getUsername());
        pstmt.setString(3, comment);
        pstmt.setString(4, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        pstmt.setInt(5, 0);
        if(rating == -1){
            pstmt.setNull(6, Types.NUMERIC);
        }else{
            pstmt.setFloat(6, rating);
            addRating(dayTourID, rating);
        }

        pstmt.executeUpdate();
    }

    /**
     * private fall til þess að bæta við rating þegar það er commentað.
     *
     * @param dayTourID ID á dagsferð
     * @param rating rating, 0 til 5
     * @throws Exception
     */
    private static void addRating(int dayTourID, float rating) throws Exception{
        getConnection();

        String sql = "UPDATE dayTours SET sumOfRatings = sumOfRatings + ?, amountOfRatings = amountOfRatings + 1 WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, Float.toString(rating));
        pstmt.setString(2, Integer.toString(dayTourID));

        pstmt.executeUpdate();
        // nú þarf að reikna Rating miðað við hvað sumOfRatings og amountOfRatings er
        // Rating er fengið með því að fá average af öllum ratings, því er það sumOfRatings / amountOfRatings

        sql = "SELECT sumOfRatings, amountOfRatings FROM dayTours WHERE ID = '" + dayTourID + "'";
        Statement s = conn.createStatement();
        ResultSet res = s.executeQuery(sql);

        float sumOfRatings = res.getFloat("sumOfRatings");
        int amountOfRatings = res.getInt("amountOfRatings");

        float averageRating = sumOfRatings / amountOfRatings;
        // námundum að næsta .5
        double roundedRating = Math.round(averageRating * 2) / 2.0;

        float r = (float) roundedRating;

        sql = "UPDATE dayTours SET Rating = ? WHERE ID = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, Float.toString(r));
        pstmt.setString(2, Integer.toString(dayTourID));

        pstmt.executeUpdate();
    }

    public static boolean hasUserLikedComment(int commentID){
        try{
            getConnection();

            Statement s = conn.createStatement();

            String query = "SELECT * FROM userLikedComment WHERE userID = '" + User.getUserID() +  "' AND commentID = '" + commentID + "'";

            ResultSet res = s.executeQuery(query);
            // þetta mun skila true ef notandinn hefur like-að commentið, annars false
            return res.next();
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fall til þess að bæta like við comment á dagsferð
     */
    public static void addLike(int commentID) throws Exception {
        getConnection();
        // fyrst bætum við like á comments töfluna
        String sql = "UPDATE comments SET likes = likes + 1 WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, Integer.toString(commentID));

        pstmt.executeUpdate();
        // svo bætum við hver var að like-a í userLikedComment töfluna
        // svo að það sé hægt að gera virkni svo að sami user
        // má ekki like-a sama comment oftar en tvisvar
        sql = "INSERT INTO userLikedComment VALUES(?,?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, Integer.toString(User.getUserID()));
        pstmt.setString(2, Integer.toString(commentID));

        pstmt.executeUpdate();
    }

    /**
     * Fall til þess að fjarlægja like við comment á dagsferð
     */
    public static void removeLike(int commentID) throws Exception {
        getConnection();
        // fyrst fjarlægjum við like á comments töflunni
        String sql = "UPDATE comments SET likes = likes - 1 WHERE ID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, Integer.toString(commentID));

        pstmt.executeUpdate();
        // svo fjarlægjum við like-ið á userLikedComment töflunni
        sql = "DELETE FROM userLikedComment WHERE userID = '" + User.getUserID() + "' AND commentID = '" + commentID + "'";
        Statement s = conn.createStatement();
        s.executeUpdate(sql);
    }

    public static void addUser(String username, String password) throws Exception {
        getConnection();

        String sql = "INSERT INTO users(username, password) VALUES(?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);

        pstmt.executeUpdate();
    }

    public static int getIDbyUser(String username) throws Exception {
        getConnection();

        String query = "SELECT userID FROM users WHERE username = '" + username + "'";

        Statement s = conn.createStatement();
        ResultSet res = s.executeQuery(query);

        return res.getInt("userID");
    }

    public static boolean doesDayTourExist(String title) {
        try{
            getConnection();

            Statement s = conn.createStatement();

            String query = "SELECT * FROM dayTours WHERE Title = '" + title + "'";

            ResultSet res = s.executeQuery(query);
            // þetta mun skila false ef dagsferðin er ekki til en true annars
            return res.next();
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Keyrist þegar notandi vill signa inn.
     * athugar hvort usernameið og passwordið passi við notenda sem er til í databaseinu.
     * @param username username.
     * @param password password.
     */
    public static boolean doesUserExist(String username, String password) {
        try{
            getConnection();

            Statement s = conn.createStatement();

            String query = "SELECT * FROM users WHERE username = '" + username +  "' AND password = '" + password + "'";

            ResultSet res = s.executeQuery(query);
            // þetta mun skila false ef notandinn er ekki til en true annars
            return res.next();
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean doesUserExist(String username) {
        try{
            getConnection();

            Statement s = conn.createStatement();

            String query = "SELECT * FROM users WHERE username = '" + username + "'";

            ResultSet res = s.executeQuery(query);
            // þetta mun skila false ef notandinn er ekki til en true annars
            return res.next();
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return false;
    }
}
