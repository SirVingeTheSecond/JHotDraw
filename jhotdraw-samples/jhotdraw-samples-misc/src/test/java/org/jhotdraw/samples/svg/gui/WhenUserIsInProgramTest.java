package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class WhenUserIsInProgramTest extends Stage<WhenUserIsInProgramTest> {
    @ExpectedScenarioState
    protected CanvasToolBar canvasToolBar;

    public WhenUserIsInProgramTest the_user_is_in_program() {
        // if we can run this we assume they are in the program
        return this;
    }
}
