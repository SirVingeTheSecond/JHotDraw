package org.jhotdraw.undo;

import org.junit.Before;
import org.junit.Test;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import static org.junit.Assert.*;

public class UndoRedoManagerTest {
    private UndoRedoManager manager;

    @Before
    public void initializeManager() {
        manager = new UndoRedoManager();
    }

    @Test
    public void initializedCorrectly() {
        assertFalse("Manager initialized with significant edits. (returned true)", manager.hasSignificantEdits());
        // can Undo/Redo checks
        assertFalse("Manager initialized with wrong canRedo() return. (returned true)", manager.canRedo());
        assertFalse("Manager initialized with wrong canUndo() return. (returned true)", manager.canUndo());
        // actions not null
        assertNotNull("Manager initialized without initializing RedoAction", manager.getRedoAction());
        assertNotNull("Manager initialized without initializing UndoAction", manager.getUndoAction());
    }

    @Test
    public void canAddSignificantEdit() {
        manager.addEdit(new AbstractUndoableEdit());
        assertTrue(manager.hasSignificantEdits());
    }

    @Test
    public void canAddInSignificantEdit() {
        manager.addEdit(new AbstractUndoableEdit() {
            @Override
            public boolean isSignificant() {return false;}
        });
        assertFalse(manager.hasSignificantEdits());
    }

    @Test
    public void canAddAndUndoMultipleEdits() {
        manager.addEdit(new AbstractUndoableEdit());
        assertTrue(manager.canUndo());
        manager.addEdit(new AbstractUndoableEdit());
        assertTrue(manager.canUndo());
        manager.undo();
        assertTrue(manager.canUndo());
    }

    @Test
    public void canUndoAndRedo() {
        manager.addEdit(new AbstractUndoableEdit());
        assertTrue(manager.canUndo());
        assertFalse(manager.canRedo());
        manager.undo();
        assertFalse(manager.canUndo());
        assertTrue(manager.canRedo());
        manager.redo();
    }

    @Test
    public void undoThrowsErrorWhenEditStackEmpty() {
        assertThrows(CannotUndoException.class, () -> manager.undo());
    }

    @Test
    public void redoThrowsErrorWhenEditStackEmpty() {
        assertThrows(CannotRedoException.class, () -> manager.redo());
    }

    @Test
    public void canDiscardAllSignificantEdits() {
        manager.addEdit(new AbstractUndoableEdit());
        assertTrue(manager.hasSignificantEdits());
        manager.discardAllEdits();
        assertFalse(manager.hasSignificantEdits());
    }

    @Test
    public void undoActionState() {
        assertFalse(manager.getUndoAction().isEnabled());
        manager.addEdit(new AbstractUndoableEdit());
        assertTrue(manager.getUndoAction().isEnabled());
    }

    @Test
    public void redoActionState() {
        assertFalse(manager.getRedoAction().isEnabled());
        manager.addEdit(new AbstractUndoableEdit());
        assertFalse(manager.getRedoAction().isEnabled());
        manager.undo();
        assertTrue(manager.getRedoAction().isEnabled());
    }
}