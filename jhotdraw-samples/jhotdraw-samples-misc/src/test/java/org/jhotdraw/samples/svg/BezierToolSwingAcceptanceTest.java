package org.jhotdraw.samples.svg;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.MouseButton;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.BezierFigure;
import org.junit.Assume;
import org.junit.BeforeClass;
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
 * Launch the application to simulate actual user
 * interactions to verify the BezierTool behaves as expected.
 *
 * Note: These tests are skipped in CI environments (CI=true) because
 * they require a display. They can be run locally.
 */
public class BezierToolSwingAcceptanceTest extends AssertJSwingJUnitTestCase {

    @BeforeClass
    public static void skipInCI() {
        Assume.assumeTrue("Skipping Swing tests in CI (no display)",
                System.getenv("CI") == null);
    }

    private FrameFixture window;

    @Override
    protected void onSetUp() throws Exception {
        GuiActionRunner.execute(() -> {
            Main.main(new String[0]);
            return null;
        });

        // to find the application window
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return frame.isShowing();
            }
        }).withTimeout(10000).using(robot());

        // The reason for a fixed window size is that Xvfb does not support window maximization.
        window.resizeTo(new Dimension(800, 600));
        robot().waitForIdle();

        pause(500);
    }

    /**
     * Helper method to pause execution for a specified duration.
     */
    private void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Finds and clicks the scribble button in the toolbar.
     */
    private void selectBezierTool() {
        // Find the button by name...
        JToggleButton scribbleButton = robot().finder().find(
                new GenericTypeMatcher<JToggleButton>(JToggleButton.class) {
                    @Override
                    protected boolean isMatching(JToggleButton button) {
                        return "createScribble".equals(button.getName());
                    }
                });
        robot().click(scribbleButton);
        robot().waitForIdle();
    }

    /**
     * Scenario: Artist draws a Bezier path using the Bezier tool
     *
     * Given the SVG application is open
     * When the artist selects the Bezier tool and draws a path
     * Then a Bezier figure should be created on the canvas
     */
    @Test
    public void artist_draws_bezier_path_with_bezier_tool() {
        // Given
        window.requireVisible();

        selectBezierTool();

        DefaultDrawingView view = robot().finder().findByType(DefaultDrawingView.class, true);
        Drawing drawing = view.getDrawing();
        int initialCount = drawing.getChildCount();

        // Get view center to ensure we click in visible area
        Dimension viewSize = view.getSize();
        int centerX = viewSize.width / 2;
        int centerY = viewSize.height / 2;

        // When - artist draws a path by clicking points relative to center
        Point p1 = new Point(centerX - 100, centerY);
        Point p2 = new Point(centerX, centerY + 50);
        Point p3 = new Point(centerX + 100, centerY);

        robot().click(view, p1, MouseButton.LEFT_BUTTON, 1);
        robot().waitForIdle();
        robot().click(view, p2, MouseButton.LEFT_BUTTON, 1);
        robot().waitForIdle();
        robot().click(view, p3, MouseButton.LEFT_BUTTON, 1);
        robot().waitForIdle();

        // Double-click to finish
        robot().click(view, p3, MouseButton.LEFT_BUTTON, 2);
        robot().waitForIdle();

        // Then
        assertThat(drawing.getChildCount())
                .as("A new figure should be created")
                .isGreaterThan(initialCount);
    }

    /**
     * Scenario: Artist draws a freehand curve by dragging
     *
     * Given the SVG application is open with Bezier tool selected
     * When the artist drags to draw a freehand path
     * Then a smoothed Bezier figure should be created
     */
    @Test
    public void artist_draws_freehand_bezier_curve_by_dragging() {
        // Given
        window.requireVisible();
        selectBezierTool();

        DefaultDrawingView view = robot().finder().findByType(DefaultDrawingView.class, true);
        Drawing drawing = view.getDrawing();
        int initialCount = drawing.getChildCount();

        // Get view center to ensure we drag in visible area
        Dimension viewSize = view.getSize();
        int centerX = viewSize.width / 2;
        int centerY = viewSize.height / 2;

        // When - artist drags to draw freehand
        Point start = new Point(centerX - 100, centerY);
        Point mid = new Point(centerX, centerY + 50);
        Point end = new Point(centerX + 100, centerY);

        robot().pressMouse(view, start, MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        robot().moveMouse(view, mid);
        robot().waitForIdle();
        robot().moveMouse(view, end);
        robot().waitForIdle();
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();

        // Double-click to finish
        robot().click(view, end, MouseButton.LEFT_BUTTON, 2);
        robot().waitForIdle();

        // Then
        assertThat(drawing.getChildCount())
                .as("A new figure should be created from freehand drawing")
                .isGreaterThan(initialCount);
    }

    /**
     * Scenario: Created figure is a BezierFigure
     *
     * Given the SVG application with a drawn path
     * Then the created figure should be a BezierFigure type
     */
    @Test
    public void created_figure_is_bezier_figure_type() {
        // Given
        window.requireVisible();
        selectBezierTool();

        DefaultDrawingView view = robot().finder().findByType(DefaultDrawingView.class, true);
        Drawing drawing = view.getDrawing();

        // Get view center
        Dimension viewSize = view.getSize();
        int centerX = viewSize.width / 2;
        int centerY = viewSize.height / 2;

        // When - draw a simple path
        Point p1 = new Point(centerX - 50, centerY);
        Point p2 = new Point(centerX + 50, centerY);

        robot().click(view, p1, MouseButton.LEFT_BUTTON, 1);
        robot().waitForIdle();
        robot().click(view, p2, MouseButton.LEFT_BUTTON, 1);
        robot().waitForIdle();
        robot().click(view, p2, MouseButton.LEFT_BUTTON, 2);
        robot().waitForIdle();

        // Then
        boolean hasBezierFigure = drawing.getChildren().stream()
                .anyMatch(figure -> figure instanceof BezierFigure);

        assertThat(hasBezierFigure)
                .as("The created figure should be a BezierFigure")
                .isTrue();
    }

    @Override
    protected void onTearDown() throws Exception {
        if (window != null) {
            window.cleanUp();
        }
    }
}
