package org.jhotdraw.draw.tool;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.figure.BezierFigure;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Acceptance test for BezierTool using AssertJ-Swing.
 *
 * User Story:
 * As an artist, I want to draw smooth Bezier curves so that I can create
 * professional vector illustrations.
 *
 * These tests user interactions with the UI to verify
 * the BezierTool behaves correctly from the user's perspective.
 */
public class BezierToolSwingAcceptanceTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private DefaultDrawingView view;
    private DefaultDrawingEditor editor;
    private BezierTool bezierTool;

    @Override
    protected void onSetUp() throws Exception {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame("Bezier Tool Acceptance Test");

            view = new DefaultDrawingView();
            DefaultDrawing drawing = new DefaultDrawing();
            view.setDrawing(drawing);

            editor = new DefaultDrawingEditor();
            editor.add(view);
            editor.setActiveView(view);

            bezierTool = BezierTool.createWithPrototype(new BezierFigure());
            editor.setTool(bezierTool);

            f.add(view.getComponent());
            f.setSize(800, 600);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setVisible(true);
            return f;
        });

        window = new FrameFixture(robot(), frame);
        window.show();
        robot().waitForIdle();
    }

    /**
     * Scenario: Tool created with factory method integrates with Swing editor
     *
     * Given a Swing drawing editor
     * When a BezierTool is created using the factory method
     * Then the tool should integrate correctly with the editor
     */
    @Test
    public void factory_created_tool_integrates_with_swing_editor() {
        // Given
        window.requireVisible();

        // Then - verify the tool integrates correctly
        assertThat(bezierTool)
                .as("BezierTool should be created")
                .isNotNull();

        assertThat(editor.getTool())
                .as("Tool should be set as active tool in editor")
                .isSameAs(bezierTool);

        assertThat(editor.getActiveView())
                .as("Editor should have an active view")
                .isSameAs(view);

        assertThat(view.getDrawing())
                .as("View should have a drawing")
                .isNotNull();
    }

    /**
     * Scenario: Tool has correct presentation name for undo operations
     *
     * Given a BezierTool created with the factory method
     * Then the tool should have a valid presentation name for Swing undo support
     */
    @Test
    public void tool_has_presentation_name_for_undo_support() {
        // Given
        window.requireVisible();

        // Then
        assertThat(bezierTool.getPresentationName())
                .as("Tool should have a presentation name for undo operations")
                .isNotNull()
                .isNotEmpty();
    }

    /**
     * Scenario: Multiple tools can be created and swapped in editor
     *
     * Given a Swing drawing editor with an active tool
     * When a new BezierTool is created and set
     * Then the editor should use the new tool
     */
    @Test
    public void tools_can_be_swapped_in_editor() {
        // Given
        window.requireVisible();
        BezierTool originalTool = bezierTool;

        // When - create and set a new tool
        BezierTool newTool = BezierTool.createWithFittedCurves(new BezierFigure(), false);
        GuiActionRunner.execute(() -> editor.setTool(newTool));
        robot().waitForIdle();

        // Then
        assertThat(editor.getTool())
                .as("Editor should now use the new tool")
                .isSameAs(newTool)
                .isNotSameAs(originalTool);
    }

    /**
     * Scenario: Factory methods create independent instances
     *
     * Given the factory methods for creating BezierTool
     * When creating tools with different configurations
     * Then each tool should be independent
     */
    @Test
    public void factory_methods_create_independent_tool_instances() {
        // When
        BezierTool tool1 = BezierTool.createWithPrototype(new BezierFigure());
        BezierTool tool2 = BezierTool.createWithPrototype(new BezierFigure());
        BezierTool tool3 = BezierTool.createWithFittedCurves(new BezierFigure(), false);

        // Then
        assertThat(tool1)
                .as("Each factory call should create a new instance")
                .isNotSameAs(tool2)
                .isNotSameAs(tool3);

        assertThat(tool2).isNotSameAs(tool3);
    }

    @Override
    protected void onTearDown() throws Exception {
        if (window != null) {
            window.cleanUp();
        }
    }
}
