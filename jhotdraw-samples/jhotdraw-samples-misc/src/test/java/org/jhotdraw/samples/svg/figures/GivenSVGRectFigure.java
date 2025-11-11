package org.jhotdraw.samples.svg.figures;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenSVGRectFigure extends Stage<GivenSVGRectFigure> {

    @ProvidedScenarioState
    SVGRectFigure rect;

    public GivenSVGRectFigure a_new_rectangle() {
        rect = new SVGRectFigure(10, 20, 100, 50, 0, 0);
        return self();
    }

    public GivenSVGRectFigure a_rectangle_with_bounds(double x, double y, double width, double height) {
        rect = new SVGRectFigure(x, y, width, height, 0, 0);
        return self();
    }
}
