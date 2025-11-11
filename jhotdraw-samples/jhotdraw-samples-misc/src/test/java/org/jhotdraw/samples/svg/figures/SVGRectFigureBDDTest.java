package org.jhotdraw.samples.svg.figures;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;
import java.awt.geom.Point2D;

public class SVGRectFigureBDDTest
        extends ScenarioTest<GivenSVGRectFigure, WhenModifyingRectangle, ThenRectangleBehavior> {

    @Test
    public void rectangle_bounds_update_correctly_when_setBounds_called() {
        given().a_new_rectangle();

        when().set_bounds(new Point2D.Double(5, 5), new Point2D.Double(50, 30));

        then().the_bounds_are(5.0, 5.0, 45.0, 25.0);
    }

    @Test
    public void rectangle_moves_correctly_after_translation() {
        given().a_new_rectangle();

        when().transform_by_translation(10, 10);

        then().the_bounds_are(20.0, 30.0, 100.0, 50.0);
    }
}
