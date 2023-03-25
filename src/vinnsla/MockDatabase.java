package vinnsla;

import java.util.Date;

public class MockDatabase {

    private static DayTour[] dt;

    public MockDatabase() {
        DayTour einn = new DayTour(1,"ABC ferð","Þetta er fyrsta ferðin",new String[0],new Date(),1234,10,10,"Austurland",12,0);
        DayTour tveir = new DayTour(1,"Peninga ferð","Þetta er dýrasta ferðin",new String[0],new Date(),100000,15,15,"Vesturland",8,0);
        DayTour þrir = new DayTour(1,"Stjörnu ferð","Þetta er besta ferðin",new String[0],new Date(),5000,20,10,"Suðurland",8,5);
        DayTour fjorir = new DayTour(1,"Norður ferð","Þetta er Norðurlands ferðin",new String[0],new Date(),4000,10,10,"Norðurland",2,3);
        DayTour fimm = new DayTour(1,"Uppbókuð ferð","Þetta er uppbókuð ferð",new String[0],new Date(),1500,10,0,"Vesturland",3,0);
        dt = new DayTour[]{einn, tveir, þrir, fjorir, fimm};
    }

    public DayTour[] getDayTours(String area, String search, String order) {
        DayTour[] filter = new DayTour[5];
        int fIndex = 0;
        for (int i = 0; i < 5; i++) {
            if ((dt[i].getLocation().equals(area) || area.equals("Allt land")) && dt[i].getTourTitle().contains(search)) {
                filter[fIndex] = dt[i];
                fIndex++;
            }
        }
        DayTour[] result = new DayTour[fIndex];
        for(int j = 0; j < fIndex; j++) {
            result[j] = filter[j];
        }

        if(order.equals("Verð")) {
            for(int i = 0; i < fIndex-1; i++) {
                int min_idx = i;
                for (int j = i + 1; j < fIndex; j++)
                    if (result[j].getPrice() < result[min_idx].getPrice())
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                DayTour temp = result[min_idx];
                result[min_idx] = result[i];
                result[i] = temp;
            }
        }

        if(order.equals("Einkunn")) {
            for(int i = 0; i < fIndex-1; i++) {
                int min_idx = i;
                for (int j = i + 1; j < fIndex; j++)
                    if (result[j].getRating() > result[min_idx].getRating())
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                DayTour temp = result[min_idx];
                result[min_idx] = result[i];
                result[i] = temp;
            }
        }

        if(order.equals("Stafrófsröð")) {
            for(int i = 0; i < fIndex-1; i++) {
                int min_idx = i;
                for (int j = i + 1; j < fIndex; j++)
                    if (result[j].getTourTitle().compareTo(result[min_idx].getTourTitle()) < 0)
                        min_idx = j;

                // Swap the found minimum element with the first
                // element
                DayTour temp = result[min_idx];
                result[min_idx] = result[i];
                result[i] = temp;
            }
        }
        return result;
    }
}
