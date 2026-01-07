/*
 * @(#)SVGAttributedFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.figure.AbstractAttributedFigure;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.AbstractAction;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.samples.svg.SVGAttributeKeys;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Base class for SVG figures. Provides opacity compositing, applies TRANSFORM,
 * and delegates fill/stroke rendering to subclasses.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class SVGAttributedFigure extends AbstractAttributedFigure {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public SVGAttributedFigure() {
    }

    @Override
    public void draw(Graphics2D g) {
        double opacity = get(OPACITY);
        opacity = Math.min(Math.max(0d, opacity), 1d);

        if (opacity == 0d) {
            return;
        }

        if (opacity == 1d) {
            drawFigure(g);
            return;
        }

        // For partial opacity, render figure into an off-screen image and composite.
        Rectangle2D.Double drawingArea = getDrawingArea();
        Rectangle2D clipBounds = g.getClipBounds();
        if (clipBounds != null) {
            Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
        }
        if (drawingArea.isEmpty()) {
            return;
        }

        // Determine device scale (assume uniform scale typical for views; still handle non-uniform safely).
        AffineTransform deviceTx = g.getTransform();
        double sx = Math.abs(deviceTx.getScaleX());
        double sy = Math.abs(deviceTx.getScaleY());
        if (sx == 0d) sx = 1d;
        if (sy == 0d) sy = 1d;

        // Add a small margin to avoid clipping due to rounding.
        final double margin = 2d;

        int pixelW = (int) Math.ceil((drawingArea.getWidth() + margin) * sx);
        int pixelH = (int) Math.ceil((drawingArea.getHeight() + margin) * sy);

        // Ensure we always allocate a valid image.
        pixelW = Math.max(1, pixelW);
        pixelH = Math.max(1, pixelH);

        BufferedImage buf = new BufferedImage(pixelW, pixelH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = buf.createGraphics();
        try {
            gr.setRenderingHints(g.getRenderingHints());

            // Map drawingArea in user space into the buffer with device scaling.
            gr.scale(sx, sy);
            gr.translate(-drawingArea.getX(), -drawingArea.getY());

            // Important: drawFigure expects a Graphics2D in user coordinates.
            drawFigure(gr);

        } finally {
            gr.dispose();
        }

        Composite savedComposite = g.getComposite();
        try {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));

            // Draw the buffer back into the original graphics in user coordinates.
            // Use the exact drawingArea location; width/height in user space.
            g.drawImage(
                    buf,
                    (int) Math.floor(drawingArea.getX()),
                    (int) Math.floor(drawingArea.getY()),
                    (int) Math.ceil(drawingArea.getWidth() + margin),
                    (int) Math.ceil(drawingArea.getHeight() + margin),
                    null
            );
        } finally {
            g.setComposite(savedComposite);
        }
    }

    /**
     * Draws the figure using current attributes.
     * This method is invoked before the rendered image is composited for opacity.
     */
    public void drawFigure(Graphics2D g) {
        AffineTransform savedTransform = null;

        AffineTransform tx = get(TRANSFORM);
        if (tx != null) {
            savedTransform = g.getTransform();
            g.transform(tx);
        }

        // Fill
        Paint fillPaint = SVGAttributeKeys.getFillPaint(this);
        if (fillPaint != null) {
            g.setPaint(fillPaint);
            drawFill(g);
        }

        // Stroke
        Paint strokePaint = SVGAttributeKeys.getStrokePaint(this);
        if (strokePaint != null && get(STROKE_WIDTH) > 0d) {
            g.setPaint(strokePaint);
            g.setStroke(SVGAttributeKeys.getStroke(this, 1.0));
            drawStroke(g);
        }

        if (tx != null) {
            g.setTransform(savedTransform);
        }
    }

    /**
     * Invalidate cached geometry in subclasses when attributes affecting rendering or
     * coordinate mapping change.
     */
    @Override
    public <T> void set(AttributeKey<T> key, T newValue) {
        boolean affectsTransform = key == TRANSFORM;

        boolean affectsOpacity = key == OPACITY;

        boolean affectsPaintOrStroke =
                key == FILL_COLOR
                        || key == FILL_GRADIENT
                        || key == STROKE_COLOR
                        || key == STROKE_GRADIENT
                        || key == STROKE_WIDTH
                        || key == STROKE_CAP
                        || key == STROKE_JOIN
                        || key == STROKE_MITER_LIMIT
                        || key == STROKE_DASHES
                        || key == STROKE_DASH_PHASE
                        || key == STROKE_TYPE;

        if (affectsTransform || affectsOpacity || affectsPaintOrStroke) {
            invalidate();
        }
        super.set(key, newValue);
    }

    @Override
    public Collection<Action> getActions(Point2D.Double p) {
        LinkedList<Action> actions = new LinkedList<Action>();
        if (get(TRANSFORM) != null) {
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
            actions.add(new AbstractAction(labels.getString("edit.removeTransform.text")) {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent evt) {
                    willChange();
                    fireUndoableEditHappened(
                            TRANSFORM.setUndoable(SVGAttributedFigure.this, null)
                    );
                    changed();
                }
            });
        }
        return actions;
    }
}
