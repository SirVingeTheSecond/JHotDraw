package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.junit.JGivenMethodRule;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Rule;
import org.junit.Test;

public class CanvasToolBarScenarioTest extends ScenarioTest<GivenUserWantsToEditCanvasTest,
        WhenUserIsInProgramTest,
        ThenCanvasToolBarShouldExistTest> {

    @Rule
    public JGivenMethodRule jGivenRule = new JGivenMethodRule();

    @Test
    public void canvas_toolbar_should_be_created() {
        given().user_wants_to_edit_canvas();
        when().the_user_is_in_program();
        then().the_canvas_toolbar_should_exist();
    }
}
