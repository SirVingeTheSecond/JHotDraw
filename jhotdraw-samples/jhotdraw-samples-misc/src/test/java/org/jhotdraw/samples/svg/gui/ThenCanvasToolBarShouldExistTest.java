package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import static org.junit.Assert.assertNotNull;

public class ThenCanvasToolBarShouldExistTest extends Stage<ThenCanvasToolBarShouldExistTest> {
    @ExpectedScenarioState
    CanvasToolBar canvasToolBar;

    public ThenCanvasToolBarShouldExistTest the_canvas_toolbar_should_exist() {
        assertNotNull("CanvasToolBar does not exist", canvasToolBar);
        return this;
    }
}
