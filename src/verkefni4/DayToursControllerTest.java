package verkefni4;

import org.junit.Assert;
import org.junit.Test;
import vinnsla.Database;
import vinnsla.DayTour;

public class DayToursControllerTest {

    @Test
    public void testShowDayTours() throws Exception {
        DayTour[] dayTours = Database.getDayTours("Allt land", "", "Einkunn");
        Assert.assertNotNull(dayTours);

        float prevRating = 5;
        // athugar hvort að einkuninn sé í réttri röð (hæsta rating fyrst)
        for(DayTour dt : dayTours){
            float currRating = dt.getRating();
            Assert.assertTrue(currRating <= 5); // rating er frá 0 til 5
            Assert.assertTrue(currRating >= 0);
            Assert.assertTrue(currRating <= prevRating);
            prevRating = currRating;
        }

        // sortar eftir verði, ódýrasta fyrst
        dayTours = Database.getDayTours("Allt land", "", "Verð");
        Assert.assertNotNull(dayTours);

        int prevPrice = 0;
        // athugar hvort að verðið sé í réttri röð
        for(DayTour dt : dayTours){
            int currPrice = dt.getPrice();
            Assert.assertTrue(currPrice >= 0); // verð er aldrei mínus tala
            Assert.assertTrue(currPrice >= prevPrice);
            prevPrice = currPrice;
        }

        dayTours = Database.getDayTours("Allt land", "", "Stafrófsröð");
        Assert.assertNotNull(dayTours);

        // ef lengdin á listanum er minni en 2, þá er hann í stafrófsröð
        if(dayTours.length >= 2) {
            // athugar hvort að listinn er í stafrófsröð
            for (int i = 0; i < dayTours.length - 1; i++) {
                // ef að compareTo skilar tölu sem er < 0, þá er dayTours[i].getTourTitle() á undan í stafrófinu
                Assert.assertTrue(dayTours[i].getTourTitle().compareTo(dayTours[i+1].getTourTitle()) <= 0);
            }
        }
    }
}
