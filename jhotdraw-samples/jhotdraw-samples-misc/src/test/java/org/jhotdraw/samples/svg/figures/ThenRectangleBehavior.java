package org.jhotdraw.samples.svg.figures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import java.awt.geom.Rectangle2D;
import static org.junit.Assert.*;

public class ThenRectangleBehavior extends Stage<ThenRectangleBehavior> {

    @ExpectedScenarioState
    Rectangle2D.Double bounds;

    public ThenRectangleBehavior the_bounds_are(double x, double y, double width, double height) {
        assertEquals(x, bounds.getX(), 0.0001);
        assertEquals(y, bounds.getY(), 0.0001);
        assertEquals(width, bounds.getWidth(), 0.0001);
        assertEquals(height, bounds.getHeight(), 0.0001);
        return self();
    }
}
