package verkefni4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import vinnsla.MockDatabase;
import vinnsla.DayTour;

public class DayToursControllerTest {

    private MockDatabase mD;

    @Before
    public void setUp(){
        mD = new MockDatabase();
    }

    @Test
    public void testEinkunn() throws Exception {
        DayTour[] dayTours = mD.getDayTours("Allt land", "", "Einkunn");
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
    }

    @Test
    public void testVerd() throws Exception {
        // sortar eftir verði, ódýrasta fyrst
        DayTour[] dayTours = mD.getDayTours("Allt land", "", "Verð");
        Assert.assertNotNull(dayTours);

        int prevPrice = 0;
        // athugar hvort að verðið sé í réttri röð
        for (DayTour dt : dayTours) {
            int currPrice = dt.getPrice();
            Assert.assertTrue(currPrice >= 0); // verð er aldrei mínus tala
            Assert.assertTrue(currPrice >= prevPrice);
            prevPrice = currPrice;
        }
    }

    @Test
    public void testStafrof() throws Exception {
        DayTour[] dayTours = mD.getDayTours("Allt land", "", "Stafrófsröð");
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

    @Test
    public void testSearch() throws Exception {
        DayTour[] dayTours = mD.getDayTours("Allt land", "ABC", "Stafrófsröð");
        Assert.assertNotNull(dayTours);

        for(int i = 0; i < dayTours.length; i++) {
            Assert.assertTrue(dayTours[i].getTourTitle().contains("ABC"));
        }
    }

    @Test
    public void testVesturland() throws Exception {
        DayTour[] dayTours = mD.getDayTours("Vesturland", "", "Stafrófsröð");
        Assert.assertNotNull(dayTours);

        for(int i = 0; i < dayTours.length; i++) {
            Assert.assertTrue(dayTours[i].getLocation().equals("Vesturland"));
        }
    }
}
