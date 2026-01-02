package org.jhotdraw.draw.tool;

import org.assertj.swing.core.MouseButton;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Drawing;
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

    @Override
    protected void onSetUp() throws Exception {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame("Bezier Tool Acceptance Test");

            view = new DefaultDrawingView();
            DefaultDrawing drawing = new DefaultDrawing();
            view.setDrawing(drawing);

            editor = new DefaultDrawingEditor();
            editor.add(view);

            BezierTool bezierTool = BezierTool.createWithPrototype(new BezierFigure());
            editor.setTool(bezierTool);

            f.add(view.getComponent());
            f.setSize(800, 600);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setVisible(true);
            return f;
        });

        window = new FrameFixture(robot(), frame);
        window.show();
    }

    /**
     * Scenario: Artist draws a simple Bezier path
     *
     * Given a drawing canvas with the Bezier tool active
     * When the artist clicks to create path nodes and double-clicks to finish
     * Then a Bezier figure should be created on the canvas
     */
    @Test
    public void artist_draws_bezier_path_by_clicking_nodes() {
        // Given
        window.requireVisible();
        Component viewComponent = view.getComponent();
        Drawing drawing = view.getDrawing();
        int initialFigureCount = drawing.getChildCount();

        // When - artist clicks to create nodes
        Point firstClick = new Point(100, 100);
        Point secondClick = new Point(200, 150);
        Point thirdClick = new Point(300, 100);

        robot().click(viewComponent, firstClick);
        robot().click(viewComponent, secondClick);
        robot().click(viewComponent, thirdClick);

        // Double-click to finish the path
        robot().click(viewComponent, thirdClick);
        robot().click(viewComponent, thirdClick);

        // Then
        assertThat(drawing.getChildCount())
                .as("A new Bezier figure should be created")
                .isGreaterThan(initialFigureCount);

        boolean hasBezierFigure = drawing.getChildren().stream()
                .anyMatch(figure -> figure instanceof BezierFigure);

        assertThat(hasBezierFigure)
                .as("The created figure should be a BezierFigure")
                .isTrue();
    }

    /**
     * Scenario: Artist draws a freehand curve by dragging
     *
     * Given a drawing canvas with the Bezier tool active
     * When the artist drags to draw a freehand path
     * Then a Bezier figure with smoothed curves should be created
     */
    @Test
    public void artist_draws_freehand_curve_by_dragging() {
        // Given
        window.requireVisible();
        Component viewComponent = view.getComponent();
        Drawing drawing = view.getDrawing();
        int initialFigureCount = drawing.getChildCount();

        // When - artist drags to draw freehand
        Point start = new Point(100, 200);
        Point mid = new Point(200, 250);
        Point end = new Point(300, 200);

        robot().pressMouse(viewComponent, start);
        robot().moveMouse(viewComponent, mid);
        robot().moveMouse(viewComponent, end);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);

        // Double-click to finish
        robot().click(viewComponent, end);
        robot().click(viewComponent, end);

        // Then
        assertThat(drawing.getChildCount())
                .as("A new figure should be created from freehand drawing")
                .isGreaterThan(initialFigureCount);
    }

    /**
     * Scenario: Artist creates a closed Bezier shape
     *
     * Given a drawing canvas with the Bezier tool active
     * When the artist clicks near the starting point to close the path
     * Then a closed Bezier figure should be created
     */
    @Test
    public void artist_creates_closed_bezier_shape() {
        // Given
        window.requireVisible();
        Component viewComponent = view.getComponent();
        Drawing drawing = view.getDrawing();

        // When - artist draws a triangle shape and closes it
        Point p1 = new Point(150, 100);
        Point p2 = new Point(250, 200);
        Point p3 = new Point(50, 200);

        robot().click(viewComponent, p1);
        robot().click(viewComponent, p2);
        robot().click(viewComponent, p3);

        // Click near start point to close
        Point closePoint = new Point(152, 102);
        robot().click(viewComponent, closePoint);

        // Then
        BezierFigure createdFigure = drawing.getChildren().stream()
                .filter(figure -> figure instanceof BezierFigure)
                .map(figure -> (BezierFigure) figure)
                .findFirst()
                .orElse(null);

        assertThat(createdFigure)
                .as("A Bezier figure should be created")
                .isNotNull();

        assertThat(createdFigure.isClosed())
                .as("The figure should be closed")
                .isTrue();
    }

    /**
     * Scenario: Tool created with factory method works correctly
     *
     * Given a BezierTool created using the new factory method
     * When the artist uses the tool to draw
     * Then the tool should function identically to the legacy constructor
     */
    @Test
    public void factory_created_tool_functions_correctly() {
        // Given - tool was set up in onSetUp() using createWithPrototype
        window.requireVisible();
        Component viewComponent = view.getComponent();
        Drawing drawing = view.getDrawing();

        // When - simple drawing operation
        robot().click(viewComponent, new Point(400, 300));
        robot().click(viewComponent, new Point(500, 350));
        robot().click(viewComponent, new Point(500, 350)); // double-click to finish
        robot().click(viewComponent, new Point(500, 350));

        // Then - verify the tool created a figure
        boolean hasFigure = drawing.getChildCount() > 0;

        assertThat(hasFigure)
                .as("Factory-created BezierTool should create figures")
                .isTrue();
    }

    @Override
    protected void onTearDown() throws Exception {
        if (window != null) {
            window.cleanUp();
        }
    }
}
