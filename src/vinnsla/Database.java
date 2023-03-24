package vinnsla;

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

public class Database {

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
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("Date"));
            DayTour dt = new DayTour(res.getInt("ID"),res.getString("Title"), res.getString("Description"), imgs, date,
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
                    String[] imgs = res.getString("Images").split(",");
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("Date"));
                    DayTour dt = new DayTour(res.getInt("ID"), res.getString("Title"), res.getString("Description"), imgs, date,
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
            String[] imgs = res.getString("Images").split(",");
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(res.getString("Date"));
            dt = new DayTour(res.getInt("ID"), res.getString("Title"), res.getString("Description"), imgs, date,
                    res.getInt("Price"), res.getInt("MaxSpots"), res.getInt("spotsLeft"), res.getString("Location"),
                    res.getInt("Duration"), res.getFloat("Rating"));
        }
        return dt;
    }

    public void addComment(int dayTourId, String user, String comment, Date date){

    }

    public static void addUser(String username, String password) throws Exception {
        getConnection();

        String sql = "INSERT INTO users(username, password) VALUES(?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);

        pstmt.executeUpdate();
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
