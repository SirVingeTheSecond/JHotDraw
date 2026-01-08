package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenUserWantsToEditCanvasTest extends Stage<GivenUserWantsToEditCanvasTest> {
    @ProvidedScenarioState
    CanvasToolBar canvasToolBar;

    public GivenUserWantsToEditCanvasTest user_wants_to_edit_canvas() {
        canvasToolBar = new CanvasToolBar();
        return this;
    }
}
