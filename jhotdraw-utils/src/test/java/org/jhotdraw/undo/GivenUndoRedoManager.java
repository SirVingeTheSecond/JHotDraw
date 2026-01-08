package org.jhotdraw.undo;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenUndoRedoManager extends Stage<GivenUndoRedoManager> {

    @ProvidedScenarioState
    UndoRedoScenarioState state = new UndoRedoScenarioState();

    public GivenUndoRedoManager the_user_has_made_at_least_one_edit() {
        state.manager = new UndoRedoManager();
        state.edit = new TestUndoableEdit();
        state.manager.addEdit(state.edit);
        return this;
    }

    public GivenUndoRedoManager the_user_has_pressed_undo() {
        state.manager.undo();
        return this;
    }

    public GivenUndoRedoManager no_edits_have_been_made() {
        state.manager = new UndoRedoManager();
        return this;
    }

}
