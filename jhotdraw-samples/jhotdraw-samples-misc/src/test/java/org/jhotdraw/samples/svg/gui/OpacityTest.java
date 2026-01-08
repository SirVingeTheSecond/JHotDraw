package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.gui.JAttributeSlider;
import org.jhotdraw.gui.JPopupButton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import javax.swing.*;
import java.awt.*;

public class OpacityTest {
    static {
        System.setProperty("java.awt.headless", "true");
    }

    private CanvasToolBar canvasToolBar;
    private JFrame frame;

    // helper method for finding the opacity slider in the CanvasToolBar
    private JAttributeSlider findOpacitySlider(Container container) {
        if (container == null) return null;

        for (Component component : container.getComponents()) {

            if (component instanceof JAttributeSlider) {
                return (JAttributeSlider) component;
            }

            if (component instanceof JPopupButton) {
                JPopupMenu menu = ((JPopupButton) component).getPopupMenu();
                JAttributeSlider found = findOpacitySlider(menu);
                if (found != null) return found;
            }

            if (component instanceof Container) {
                JAttributeSlider found = findOpacitySlider((Container) component);
                if (found != null) return found;
            }
        }
        return null;
    }

    @Before
    public void setup() {
        canvasToolBar = new CanvasToolBar();
        frame = new JFrame();
        frame.add(canvasToolBar);

        DrawingEditor editor = Mockito.mock(DrawingEditor.class);
        canvasToolBar.setEditor(editor);
    }

    @After
    public void tearDown() {
        canvasToolBar.removeAll();
        frame.removeAll();
        frame.remove(frame);
    }

    @Test
    public void canvasToolBarCreation() {
        assertNotNull("CanvasToolBar does not exist",canvasToolBar);
    }

    @Test
    public void opacitySliderCreation() {
        JComponent component = canvasToolBar.createDisclosedComponent(2);
        assertNotNull(component);

        JAttributeSlider slider = findOpacitySlider(component);
        assertNotNull("Opacity slider does not exists", slider);
    }

    @Test
    public void opacitySliderValue() {
        JComponent component = canvasToolBar.createDisclosedComponent(2);
        JAttributeSlider slider = findOpacitySlider(component);

        assertNotNull(slider);
        assertEquals(0, slider.getMinimum());
        assertEquals(100, slider.getMaximum());

        slider.setValue(50);
        assertEquals(50, slider.getValue());
    }
}