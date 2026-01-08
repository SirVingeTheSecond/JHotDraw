package org.jhotdraw.undo;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import java.awt.event.ActionEvent;

/**
 * Redo Action for use in a menu bar.
 */
class RedoAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final UndoRedoManager undoRedoManager;

    public RedoAction(UndoRedoManager undoRedoManager) {
        this.undoRedoManager = undoRedoManager;
        UndoRedoManager.getLabels().configureAction(this, "edit.redo");
        setEnabled(false);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        try {
            this.undoRedoManager.redo();
        } catch (CannotRedoException e) {
            System.out.println("Cannot redo: " + e);
        }
    }
}