package org.jhotdraw.samples.svg.figures;

import junit.framework.TestCase;
import org.jhotdraw.draw.AttributeKeys;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

public class SVGEllipseFigureTest extends TestCase {

    public void testGetBounds() {
        SVGEllipseFigure f = new SVGEllipseFigure(10, 20, 30, 40);
        Rectangle2D.Double b = f.getBounds();

        assertEquals(10.0, b.x);
        assertEquals(20.0, b.y);
        assertEquals(30.0, b.width);
        assertEquals(40.0, b.height);
    }

    public void testGetDrawingArea() {
        SVGEllipseFigure f = new SVGEllipseFigure(0, 0, 100, 50);

        f.set(AttributeKeys.STROKE_COLOR, Color.BLACK);
        f.set(AttributeKeys.STROKE_WIDTH, 1d);

        Rectangle2D.Double bounds = f.getBounds();
        Rectangle2D.Double da1 = f.getDrawingArea();

        assertTrue("Drawing area should contain bounds", da1.contains(bounds));

        f.set(AttributeKeys.STROKE_WIDTH, 12d);
        Rectangle2D.Double da2 = f.getDrawingArea();

        assertTrue("Drawing area should grow when stroke width increases",
                da2.width > da1.width && da2.height > da1.height);
    }

    public void testCreateHandles() {
        SVGEllipseFigure f = new SVGEllipseFigure(0, 0, 100, 50);


        assertNotNull(f.createHandles(0));
        assertTrue("Expected handles for detailLevel=0", f.createHandles(0).size() > 0);

        assertNotNull(f.createHandles(1));
        assertTrue("Expected handles for detailLevel=1", f.createHandles(1).size() > 0);

        assertNotNull(f.createHandles(-1));
        assertTrue("Expected handles for detailLevel=-1", f.createHandles(-1).size() > 0);
    }

    public void testTestClone() {
        SVGEllipseFigure f = new SVGEllipseFigure(0, 0, 100, 50);
        f.set(AttributeKeys.STROKE_COLOR, Color.BLACK);
        f.set(AttributeKeys.STROKE_WIDTH, 2d);

        f.contains(new Point2D.Double(10, 10));
        assertNotNull("Expected transformed cache to be created", getPrivateField(f, "cachedTransformedShape"));
        assertNotNull("Expected hit cache to be created", getPrivateField(f, "cachedHitShape"));

        SVGEllipseFigure cloned = f.clone();
        assertNotNull(cloned);

        assertNull("Clone must reset cachedTransformedShape", getPrivateField(cloned, "cachedTransformedShape"));
        assertNull("Clone must reset cachedHitShape", getPrivateField(cloned, "cachedHitShape"));

        assertEquals(f.getBounds(), cloned.getBounds());
    }

    public void testInvalidate() {
        SVGEllipseFigure f = new SVGEllipseFigure(0, 0, 100, 50);
        f.set(AttributeKeys.STROKE_COLOR, Color.BLACK);
        f.set(AttributeKeys.STROKE_WIDTH, 2d);

        f.contains(new Point2D.Double(10, 10));
        assertNotNull(getPrivateField(f, "cachedTransformedShape"));
        assertNotNull(getPrivateField(f, "cachedHitShape"));

        f.invalidate();
        assertNull("invalidate() must clear cachedTransformedShape", getPrivateField(f, "cachedTransformedShape"));
        assertNull("invalidate() must clear cachedHitShape", getPrivateField(f, "cachedHitShape"));

        f.contains(new Point2D.Double(10, 10));
        assertNotNull(getPrivateField(f, "cachedHitShape"));

        f.set(AttributeKeys.STROKE_WIDTH, 10d);
        assertNull("Changing STROKE_WIDTH must invalidate cachedHitShape", getPrivateField(f, "cachedHitShape"));

        f.contains(new Point2D.Double(10, 10));
        assertNotNull(getPrivateField(f, "cachedTransformedShape"));
        assertNotNull(getPrivateField(f, "cachedHitShape"));

        f.set(AttributeKeys.TRANSFORM, AffineTransform.getTranslateInstance(10, 10));
        assertNull("Changing TRANSFORM must invalidate cachedTransformedShape", getPrivateField(f, "cachedTransformedShape"));
        assertNull("Changing TRANSFORM must invalidate cachedHitShape", getPrivateField(f, "cachedHitShape"));
    }

    private static Object getPrivateField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            fail("Failed to read private field '" + fieldName + "': " + e);
            return null;
        }
    }
}
