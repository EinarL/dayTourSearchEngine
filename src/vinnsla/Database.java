package vinnsla;

import java.sql.*;
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
}
