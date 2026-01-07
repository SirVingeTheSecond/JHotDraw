package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;

public class WhenUserIsInProgramTest extends Stage<WhenUserIsInProgramTest> {
    protected CanvasToolBar canvasToolBar;

    public WhenUserIsInProgramTest the_user_is_in_program() {
        // if we can run this we assume they are in the program
        return this;
    }
}
