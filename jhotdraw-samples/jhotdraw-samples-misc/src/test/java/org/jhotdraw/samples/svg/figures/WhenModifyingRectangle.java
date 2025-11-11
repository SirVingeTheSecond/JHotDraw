package org.jhotdraw.samples.svg.figures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class WhenModifyingRectangle extends Stage<WhenModifyingRectangle> {

    @ExpectedScenarioState
    SVGRectFigure rect;

    @ProvidedScenarioState
    Rectangle2D.Double bounds;

    public WhenModifyingRectangle set_bounds(Point2D.Double anchor, Point2D.Double lead) {
        rect.setBounds(anchor, lead);
        bounds = rect.getBounds();
        return self();
    }

    public WhenModifyingRectangle transform_by_translation(double dx, double dy) {
        rect.transform(java.awt.geom.AffineTransform.getTranslateInstance(dx, dy));
        bounds = rect.getBounds();
        return self();
    }
}
