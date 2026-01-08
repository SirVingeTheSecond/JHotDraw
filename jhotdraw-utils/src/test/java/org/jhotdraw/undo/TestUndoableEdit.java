package org.jhotdraw.undo;

import javax.swing.undo.AbstractUndoableEdit;

public class TestUndoableEdit extends AbstractUndoableEdit {
    boolean wasUndone = false;
    boolean wasRedone = false;

    @Override
    public void undo() {
        super.undo();
        wasUndone = true;
    }

    @Override
    public void redo() {
        super.redo();
        wasRedone = true;
    }
}