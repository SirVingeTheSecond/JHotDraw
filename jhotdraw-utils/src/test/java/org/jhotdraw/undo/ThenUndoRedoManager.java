package org.jhotdraw.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import static org.junit.Assert.*;

public class ThenUndoRedoManager extends Stage<ThenUndoRedoManager> {
    @ExpectedScenarioState
    UndoRedoScenarioState state;

    public ThenUndoRedoManager the_drawing_should_return_to_the_previous_state() {
        assertTrue(state.edit.wasUndone);
        return this;
    }

    public  ThenUndoRedoManager the_state_should_return_to_before_the_undo() {
        assertTrue(state.edit.wasRedone);
        return this;
    }

    public ThenUndoRedoManager the_program_throws_the_corrosponding_error() {
        assertNotNull(state.thrownException);
        return this;
    }
}
