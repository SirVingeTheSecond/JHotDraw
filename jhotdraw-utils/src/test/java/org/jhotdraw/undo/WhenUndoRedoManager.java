package org.jhotdraw.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class WhenUndoRedoManager extends Stage<WhenUndoRedoManager> {
    @ExpectedScenarioState
    UndoRedoScenarioState state;

    public WhenUndoRedoManager the_user_presses_undo() {
        try {
            state.manager.undo();
        } catch (CannotUndoException e) {
            state.thrownException = e;
        }
        return this;
    }

    public WhenUndoRedoManager the_user_presses_redo() {
        try {
            state.manager.redo();
        } catch (CannotRedoException e) {
            state.thrownException = e;
        }
        return this;
    }
}
