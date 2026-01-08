package org.jhotdraw.undo;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

public class UndoRedoManagerScenarioTest
        extends ScenarioTest<GivenUndoRedoManager, WhenUndoRedoManager, ThenUndoRedoManager> {

    @Test
    public void undo_returns_to_previous_state() {
        given().the_user_has_made_at_least_one_edit();
        when().the_user_presses_undo();
        then().the_drawing_should_return_to_the_previous_state();
    }

    @Test
    public void redo_returns_to_state_before_undo() {
        given().the_user_has_made_at_least_one_edit().and().the_user_has_pressed_undo();
        when().the_user_presses_redo();
        then().the_state_should_return_to_before_the_undo();
    }

    @Test
    public void undo_throws_an_error() {
        given().no_edits_have_been_made();
        when().the_user_presses_undo();
        then().the_program_throws_the_corrosponding_error();
    }

}
