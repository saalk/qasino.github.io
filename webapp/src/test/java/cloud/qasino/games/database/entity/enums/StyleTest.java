package cloud.qasino.games.database.entity.enums;


import cloud.qasino.games.database.entity.enums.game.Style;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleTest {

    @Test
    public void callFromLabelShouldBecomeDefault() {

        // assert statements
        assertEquals("nrrn22", Style.fromLabelWithDefault("").getLabel());
        //      assertEquals("hraqn3", tester.fromLabel("h").getLabel());
 /*     assertEquals("hraqn3", tester.fromLabel("hr").getLabel());
        assertEquals("hraqn3", tester.fromLabel("hra").getLabel());
        assertEquals("hraqn3", tester.fromLabel("hraq").getLabel());
        assertEquals("hraqn3", tester.fromLabel("hraqn").getLabel());
        assertEquals("hraqn3", tester.fromLabel("hraqn3").getLabel());
*/
    }
}
