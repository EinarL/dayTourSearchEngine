package vinnsla;

import java.util.*;

public class Data {

    private static ArrayList<DayTour> allDayTours = null; // listi yfir öllum dayTours
    private static ArrayList<DayTour> currentDayTours = null; // listi yfir þeim sem við erum að birta

    public static void setDayTours(ArrayList<DayTour> dt){
        allDayTours = dt;
        currentDayTours = dt;
    }

    public static ArrayList<DayTour> getDayTours(){
        return currentDayTours;
    }

    public static void addDayTour(DayTour dt){
        allDayTours.add(dt);
    }

    public static void replaceDayTour(String dtTitleToReplace, DayTour replacementDT){
        for(int i = 0; i < allDayTours.size(); i++){
            if(allDayTours.get(i).getTourTitle().equals(dtTitleToReplace)){
                allDayTours.set(i, replacementDT);
                break;
            }
        }
    }

    public static void editSpotsLeft(String tourName, int spots){
        for(DayTour dt : allDayTours){
            if(tourName.equals(dt.getTourTitle())){
                dt.setSpotsLeft(spots);
                break;
            }
        }
    }

    public static void updateRating(int dtID, float rating){
        if(allDayTours == null) return;
        for(DayTour dt : allDayTours){
            if(dtID == dt.getId()){
                dt.setRating(rating);
                break;
            }
        }
    }

    public static ArrayList<DayTour> getToursBasedOnInput(String s, String location, String order){
        setFromLocation(location);
        sort(order);
        return search(s);
    }

    private static ArrayList<DayTour> search(String s){
        if(s.equals("")) return currentDayTours;
        ArrayList<DayTour> newCurrentDayTours = new ArrayList<>();
        for(DayTour dt : currentDayTours){
            if(dt.getTourTitle().toLowerCase().contains(s.toLowerCase())){
                newCurrentDayTours.add(dt);
            }
        }
        currentDayTours = newCurrentDayTours;
        return currentDayTours;
    }

    /**
     * setur þær dagsferðir í currentDayTours sem eru í location landssvæðinu
     * @param location landssvæði
     */
    private static void setFromLocation(String location){ // "Allt land", "Vesturland", "Norðurland", "Suðurland", eða "Austurland"
        if (location.equals("Allt land")){
            currentDayTours = allDayTours;
            return;
        }
        currentDayTours = new ArrayList<>();
        for (DayTour dt : allDayTours){
            if(dt.getLocation().equals(location)){
                currentDayTours.add(dt);
            }
        }
    }

    private static void sort(String order){ // Einkunn, Verð eða Stafrófsröð
        if(order.equals("Einkunn"))
            currentDayTours.sort(Comparator.comparingDouble(DayTour::getRating).reversed());
        else if(order.equals("Verð"))
            currentDayTours.sort(Comparator.comparingInt(DayTour::getPrice));
        else if(order.equals("Stafrófsröð"))
            currentDayTours.sort(Comparator.comparing(DayTour::getTourTitle));
        else
            System.err.println("order á að vera Einkunn, Verð eða Stafrófsröð, ekki " + order);
    }
}
