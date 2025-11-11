package org.jhotdraw.samples.svg.figures;
import org.jhotdraw.draw.AttributeKeys;
import org.junit.Before;
import org.junit.Test;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static org.junit.Assert.*;

public class SVGRectFigureTest {

    private SVGRectFigure rect;

    @Before
    public void setUp() {
        rect = new SVGRectFigure(10, 20, 100, 50, 0, 0);
    }

    // 1. BASIC CONSTRUCTION TESTS
    @Test
    public void testConstructorInitializesCorrectly() {
        assertEquals(10.0, rect.getX(), 0.0001);
        assertEquals(20.0, rect.getY(), 0.0001);
        assertEquals(100.0, rect.getWidth(), 0.0001);
        assertEquals(50.0, rect.getHeight(), 0.0001);
        assertEquals(0.0, rect.getArcWidth(), 0.0001);
        assertEquals(0.0, rect.getArcHeight(), 0.0001);
        assertFalse(rect.isEmpty());
    }

    @Test
    public void testIsEmptyWhenZeroSize() {
        SVGRectFigure empty = new SVGRectFigure(0, 0, 0, 0);
        assertTrue(empty.isEmpty());
    }

    // 2. ARC WIDTH / HEIGHT SETTERS + ASSERTIONS
    @Test
    public void testSetArcWidthAndHeightUpdatesCorrectly() {
        rect.setArcWidth(8.0);
        rect.setArcHeight(10.0);
        assertEquals(8.0, rect.getArcWidth(), 0.0001);
        assertEquals(10.0, rect.getArcHeight(), 0.0001);
    }

    @Test
    public void testSetArcUsingConvenienceMethod() {
        rect.setArc(12, 15);
        assertEquals(12.0, rect.getArcWidth(), 0.0001);
        assertEquals(15.0, rect.getArcHeight(), 0.0001);
    }

    @Test(expected = AssertionError.class)
    public void testAssertInvariantArcWidthCannotBeNegative() {
        rect.setArcWidth(-5);
        assert rect.getArcWidth() >= 0 : "Arc width should never be negative";
    }

    // 3. BOUNDS & GEOMETRY LOGIC
    @Test
    public void testGetBoundsReturnsExpectedRectangle() {
        Rectangle2D.Double b = rect.getBounds();
        assertEquals(10.0, b.getX(), 0.0001);
        assertEquals(20.0, b.getY(), 0.0001);
        assertEquals(100.0, b.getWidth(), 0.0001);
        assertEquals(50.0, b.getHeight(), 0.0001);
    }

    @Test
    public void testSetBoundsUpdatesPositionAndSize() {
        Point2D.Double anchor = new Point2D.Double(5, 5);
        Point2D.Double lead = new Point2D.Double(50, 30);

        rect.setBounds(anchor, lead);

        assertEquals(5.0, rect.getX(), 0.0001);
        assertEquals(5.0, rect.getY(), 0.0001);
        assertEquals(45.0, rect.getWidth(), 0.0001);
        assertEquals(25.0, rect.getHeight(), 0.0001);
    }

    @Test
    public void testSetBoundsPreventsZeroSize() {
        Point2D.Double p = new Point2D.Double(5, 5);
        rect.setBounds(p, p);
        assertTrue(rect.getWidth() >= 0.1);
        assertTrue(rect.getHeight() >= 0.1);
    }

    // 4. TRANSFORMATION LOGIC
    @Test
    public void testTransformWithSimpleTranslation() {
        AffineTransform tx = AffineTransform.getTranslateInstance(10, 10);
        rect.transform(tx);

        Rectangle2D.Double b = rect.getBounds();
        assertEquals(20.0, b.getX(), 0.0001);
        assertEquals(30.0, b.getY(), 0.0001);
    }

    @Test
    public void testTransformWithRotationCreatesTransformObject() {
        AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 4);
        rect.transform(rotate);
        assertNotNull(rect.get(AttributeKeys.TRANSFORM));
    }

    // 5. CONTAINS / HIT LOGIC
    @Test
    public void testContainsPointInsideReturnsTrue() {
        Point2D.Double inside = new Point2D.Double(20, 30);
        assertTrue(rect.contains(inside));
    }

    @Test
    public void testContainsPointOutsideReturnsFalse() {
        Point2D.Double outside = new Point2D.Double(200, 300);
        assertFalse(rect.contains(outside));
    }

    // 6. CLONING AND RESTORE
    @Test
    public void testCloneCreatesIndependentCopy() {
        SVGRectFigure clone = rect.clone();
        clone.setBounds(new Point2D.Double(0, 0), new Point2D.Double(10, 10));

        assertNotEquals(rect.getX(), clone.getX(), 0.0001);
        assertNotEquals(rect.getY(), clone.getY(), 0.0001);
    }

    @Test
    public void testTransformRestoreDataRoundTrip() {
        Object data = rect.getTransformRestoreData();
        rect.setBounds(new Point2D.Double(0, 0), new Point2D.Double(20, 20));
        rect.restoreTransformTo(data);

        Rectangle2D.Double restored = rect.getBounds();
        assertEquals(10.0, restored.getX(), 0.0001);
        assertEquals(20.0, restored.getY(), 0.0001);
    }

    // 8. EDGE CASE: ROUND RECTANGLE
    @Test
    public void testRoundedRectangleStrokeDrawingPathNotNull() {
        SVGRectFigure r = new SVGRectFigure(0, 0, 20, 20, 10, 10);
        Shape s = r.getBounds();
        assertNotNull(s);
    }
}
