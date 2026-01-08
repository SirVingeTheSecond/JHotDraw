package org.jhotdraw.undo;

import javax.swing.*;
import java.util.logging.Logger;

public abstract class AbstractUndoRedoAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private final UndoRedoManager undoRedoManager;

    public AbstractUndoRedoAction(UndoRedoManager undoRedoManager, String actionName) {
        this.undoRedoManager = undoRedoManager;
        UndoRedoManager.getLabels().configureAction(this, "edit." + actionName);
        setEnabled(false);
    }

    protected UndoRedoManager getUndoRedoManager() {return undoRedoManager;}
    protected Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
}
