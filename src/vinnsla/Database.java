package vinnsla;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Database {

    /**
     * Þessi aðferð skilar dagsferðum sem tilheyra tilteknu landssvæði.
     * @param area fá aðeins þá sem tilheyra þessu landssvæði,
     *             getur verið: "Allt land","Vesturland","Norðurland","Suðurland" eða "Austurland",
     *             ef það er "Allt land", þá skilar aðferðin öllum dagsferðum.
     * @return skilar þeim dagsferðum sem tilheyra tilteknu landssvæði.
     */
    public static DayTour[] getDayToursByArea(String area) throws Exception{
        Class.forName("org.sqlite.JDBC");

        Connection conn = null;
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:src/database/dayTourSearchEngine.db");
            Statement s = conn.createStatement();
            String query = "SELECT * FROM dayTours WHERE Location = '" + area + "'";
            if (area.equals("Allt land")){
                query = "SELECT * FROM dayTours";
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

    /**
     *  Þessi aðferð skilar þeim dagsferðum sem innihalda strenginn s í nafninu sínu?
     *  við eigum eftir að ákveða hvort við viljum líka leita í t.d. description.
     * @param s Strengur til að leita eftir.
     * @return
     */
    public DayTour[] getDayToursBySearch(String s){

        return null;
    }
}
