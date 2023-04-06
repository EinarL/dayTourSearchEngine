package vinnsla;

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
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }

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
            String[] imgs = res.getString("images").split(",");
            Image[] images = new Image[imgs.length];
            for(int i = 0; i < images.length; i++){
                images[i] = new Image(imgs[i]);
            }
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("Date"));
            DayTour dt = new DayTour(res.getInt("ID"),res.getString("Title"), res.getString("Description"), images, date,
                                    res.getInt("Price"), res.getInt("MaxSpots"), res.getInt("spotsLeft"), res.getString("Location"),
                                    res.getInt("Duration"), res.getFloat("Rating"));
            dayTourArray.add(dt);
        }
        return dayTourArray.toArray(DayTour[]::new);
    }

    public static void addDayTour(String title, String description, String image, String date, int price, int maxSpots,
                           int spotsLeft, String location, int duration, float rating) throws Exception {
        getConnection();

        Statement s = conn.createStatement();
        String query = "SELECT MAX(id) from dayTours";
        ResultSet rs = s.executeQuery(query);
        int id = rs.getInt(1) + 1;

        s = conn.createStatement();
        String sql = "INSERT INTO dayTours(ID, Title, Description, Images, Date, Price, MaxSpots, SpotsLeft, Location, Duration, Rating) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.setString(2, title);
        pstmt.setString(3, description);
        pstmt.setString(4, image);
        pstmt.setString(5, date);
        pstmt.setInt(6, price);
        pstmt.setInt(7, maxSpots);
        pstmt.setInt(8, spotsLeft);
        pstmt.setString(9, location);
        pstmt.setInt(10, duration);
        pstmt.setFloat(11, rating);
        pstmt.executeUpdate();

    }


    public static void addBooking(String user, int numParticiapants, String tourName){
        try{
            getConnection();

            Statement s = conn.createStatement();

            String query = "SELECT SpotsLeft from dayTours WHERE title = '" + tourName + "'";
            ResultSet rs = s.executeQuery(query);
            int spots = rs.getInt(1);
            int newSpots = spots - numParticiapants;

            String sql = "UPDATE dayTours SET SpotsLeft = " + newSpots + " WHERE title = '" + tourName + "'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            query = "SELECT ID FROM dayTours WHERE Title='" + tourName +"'";
            rs = s.executeQuery(query);
            int id = rs.getInt(1);

            sql = "INSERT INTO bookings VALUES(?,?,?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setInt(2, id);
            pstmt.setInt(3, numParticiapants);
            pstmt.executeUpdate();


        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void removeBooking(String user, String tourName){
        try{
            getConnection();
            Statement s = conn.createStatement();

            String query = "SELECT ID FROM dayTours WHERE title = '" + tourName + "'";
            ResultSet rs = s.executeQuery(query);
            int dayTourId = rs.getInt(1);

            query = "SELECT numParticipants FROM bookings WHERE username = '" + user + "' AND dayTourID = " + dayTourId;
            rs = s.executeQuery(query);
            int numParticipants = rs.getInt(1);

            query = "SELECT spotsLeft FROM dayTours WHERE title = '" + tourName + "'";
            rs = s.executeQuery(query);
            int oldSpots = rs.getInt(1);
            int newSpots = numParticipants + oldSpots;

            String sql = "UPDATE dayTours SET spotsLeft = " + newSpots + " WHERE title = '" + tourName + "'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            sql = "DELETE FROM bookings WHERE username = '" + user + "' AND dayTourID = " + dayTourId + " AND numParticipants = " + numParticipants;
            PreparedStatement pstmt2 = conn.prepareStatement(sql);
            pstmt2.executeUpdate();

        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static DayTour[] getDayToursByUserBooked(String user) throws ClassNotFoundException, SQLException {

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
                    String[] imgs = res.getString("images").split(",");
                    Image[] images = new Image[imgs.length];
                    for(int i = 0; i < images.length; i++){
                        images[i] = new Image(imgs[i]);
                    }
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("Date"));
                    DayTour dt = new DayTour(res.getInt("ID"), res.getString("Title"), res.getString("Description"), images, date,
                            res.getInt("Price"), res.getInt("MaxSpots"), res.getInt("spotsLeft"), res.getString("Location"),
                            res.getInt("Duration"), res.getFloat("Rating"));
                    dayTourArray.add(dt);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dayTourArray.toArray(DayTour[]::new);
    }

    public static DayTour getDayTourByTitle(String title) throws ClassNotFoundException, SQLException, ParseException {
        getConnection();
        Statement s = conn.createStatement();
        String query = "SELECT * from dayTours WHERE title = '" + title + "'";
        ResultSet res = s.executeQuery(query);
        DayTour dt = null;
        while(res.next()){
            String[] imgs = res.getString("images").split(",");
            Image[] images = new Image[imgs.length];
            for(int i = 0; i < images.length; i++){
                images[i] = new Image(imgs[i], 588, 444, false, false); // breidd og hæð á myndinni
            }
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("Date"));
            dt = new DayTour(res.getInt("ID"), res.getString("Title"), res.getString("Description"), images, date,
                    res.getInt("Price"), res.getInt("MaxSpots"), res.getInt("spotsLeft"), res.getString("Location"),
                    res.getInt("Duration"), res.getFloat("Rating"));
        }
        return dt;
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

    public static Comment[] getCommentsByTour(int tourID) throws Exception{
        getConnection();
        Statement s = conn.createStatement();

        String query = "SELECT * from comments WHERE dayTourID = '" + tourID + "'";

        ResultSet res = s.executeQuery(query);
        ArrayList<Comment> commentArray = new ArrayList<>();
        while(res.next()){
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("date"));

            float rating = res.getFloat("rating");
            if(res.wasNull()){ // ef að rating er NULL, setjum það sem -1
                rating = -1;
            }
            System.out.println("rating is: " + rating);
            Comment comment = new Comment(res.getInt("ID"), res.getString("userCommented"),
                    res.getInt("dayTourID"), res.getString("commentText"), res.getInt("likes"), date, rating);
            commentArray.add(comment);
        }

        return commentArray.toArray(Comment[]::new);
    }

    public static void addComment(int dayTourId, String comment, float rating) throws Exception {
        getConnection();
        String sql = "INSERT INTO comments(dayTourID, userCommented, commentText, date, likes, rating) VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, dayTourId);
        pstmt.setString(2, User.getUsername());
        pstmt.setString(3, comment);
        pstmt.setString(4, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        pstmt.setInt(5, 0);
        if(rating == -1){
            pstmt.setNull(6, Types.NUMERIC);
        }else{
            pstmt.setFloat(6, rating);
        }

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
        System.err.println("Villa í doesUserExist() fallinu í Database");
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
        System.err.println("Villa í doesUserExist() fallinu í Database");
        return false;
    }
}
