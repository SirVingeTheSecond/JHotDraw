package org.jhotdraw.undo;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;

/**
 * Undo Action for use in a menu bar.
 */
class UndoAction extends AbstractUndoRedoAction {
    public UndoAction(UndoRedoManager undoRedoManager) {
        super(undoRedoManager, "undo");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            getUndoRedoManager().undo();
        } catch (CannotUndoException e) {
            System.err.println("Cannot undo: " + e);
            e.printStackTrace();
        }
    }
}