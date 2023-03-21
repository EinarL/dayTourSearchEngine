package vinnsla;

import java.awt.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Database {

    /**
     * Þessi aðferð skilar dagsferðum sem tilheyra landssvæðinu 'area'
     * og hafa titil sem er líkt og 'search'. Þeim dagsferðum eru röðuð eftir 'order'.
     * @param area fá aðeins þá sem tilheyra þessu landssvæði,
     *             getur verið: "Allt land","Vesturland","Norðurland","Suðurland" eða "Austurland",
     *             ef það er "Allt land", þá skilar aðferðin öllum dagsferðum.
     * @param search leitarstrengur fyrir Title í dagsferð.
     * @param order Strengur sem segir til um hvort á að raða eftir Einkunn eða Stafrófsröð.
     * @return skilar lista af dagsferðum.
     */
    public static DayTour[] getDayTours(String area, String search, String order) throws Exception{
        Class.forName("org.sqlite.JDBC");

        String orderBy = switch (order) {
            case "Einkunn" -> "Rating DESC";
            case "Stafrófsröð" -> "Title";
            default -> null;
        };

        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:src/database/dayTourSearchEngine.db");
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


        }catch(SQLException e){
            System.err.println(e.getMessage());
        }finally{
            try{
                if (conn != null){
                    conn.close();
                }
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
        }

        return null;
    }

    public static void addDayTour(String title, String description, String image, String date, int price, int maxSpots,
                           int spotsLeft, String location, int duration,float rating) throws ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");
        Connection conn = null;
        try{

            conn = DriverManager.getConnection("jdbc:sqlite:src/database/dayTourSearchEngine.db");

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

        }catch(SQLException e){
            System.err.println(e.getMessage());
        }finally{
            try{
                if (conn != null){
                    conn.close();
                }
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
        }

    }

    public void addBooking(String user, int numParticiapants, int dayTourId){

    }

    public void removeBooking(String user, int dayTourId){

    }

    public void addComment(int dayTourId, String user, String comment, Date date){

    }

    public void addUser(String username){

    }


    /*
    public static void main(String[] args) throws ClassNotFoundException {
        addDayTour("Test", "Þetta er test til að sjá hvort hægt sé að bæta við dayTour", "https://media.tacdn.com/media/attractions-splice-spp-674x446/0d/0a/2a/fd.jpg",
                "03/07/2023", 10490, 65, 65, "Austurland", 5, 4);
    }
    */


}
