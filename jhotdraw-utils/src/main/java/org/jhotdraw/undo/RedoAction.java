package org.jhotdraw.undo;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

/**
 * Redo Action for use in a menu bar.
 */
class RedoAction extends AbstractUndoRedoAction {
    public RedoAction(UndoRedoManager undoRedoManager) {
        super(undoRedoManager, "redo");
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            getUndoRedoManager().redo();
        } catch (CannotRedoException e) {
            getLogger().log(Level.SEVERE, "Cannot redo:" + e);
        }
    }
}