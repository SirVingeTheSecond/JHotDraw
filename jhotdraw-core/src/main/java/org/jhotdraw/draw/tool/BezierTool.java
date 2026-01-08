/*
 * @(#)BezierTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.BezierFigure;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.undo.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.Bezier;
import org.jhotdraw.geom.BezierPath;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.*;

/**
 * A {@link Tool} which allows to create a new {@link BezierFigure} by drawing its path.
 * <p>
 * To creation of the BezierFigure can be finished by adding a segment which closes the path, or by
 * double clicking on the drawing area, or by selecting a different tool in the DrawingEditor.
 * <p>
 * This class has been refactored to address the following code smells:
 * <ul>
 *   <li>Long Parameter List - addressed via Introduce Parameter Object (BezierToolConfiguration)</li>
 *   <li>Constructor Over-reliance - addressed via Replace Constructors with Creation Methods</li>
 *   <li>Long Method - addressed via Compose Method / Extract Method on mousePressed</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierTool extends AbstractTool {

    private static final long serialVersionUID = 1L;

    /**
     * Set this to true to turn on debugging output on System.out.
     */
    private static final boolean DEBUG = false;

    private Boolean finishWhenMouseReleased;
    protected Map<AttributeKey<?>, Object> attributes;
    private boolean isToolDoneAfterCreation;

    /**
     * The prototype for new figures.
     */
    private BezierFigure prototype;

    /**
     * The created figure.
     */
    protected BezierFigure createdFigure;
    private int nodeCountBeforeDrag;

    /**
     * A localized name for this tool. The presentationName is displayed by the UndoableEdit.
     */
    private String presentationName;
    private Point mouseLocation;

    /**
     * Holds the view on which we are currently creating a figure.
     */
    private DrawingView creationView;
    private final boolean calculateFittedCurveAfterCreation;

    // Factory methods (Refactoring 2: eeplace constructors with creation methods)

    /**
     * Creates a BezierTool with just a prototype figure.
     * Uses default settings for all other parameters.
     *
     * @param prototype The prototype figure used to create new BezierFigures
     * @return a new BezierTool instance
     */
    public static BezierTool createWithPrototype(BezierFigure prototype) {
        BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                .build();
        return new BezierTool(config);
    }

    /**
     * Creates a BezierTool with a prototype and custom attributes.
     *
     * @param prototype The prototype figure used to create new BezierFigures
     * @param attributes Custom attributes to apply to created figures
     * @return a new BezierTool instance
     */
    public static BezierTool createWithAttributes(BezierFigure prototype,
                                                  Map<AttributeKey<?>, Object> attributes) {
        BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                .attributes(attributes)
                .build();
        return new BezierTool(config);
    }

    /**
     * Creates a BezierTool configured for freehand drawing with curve fitting.
     * This mode smooths the drawn path using Bezier curve fitting algorithms.
     *
     * @param prototype The prototype figure used to create new BezierFigures
     * @param calculateFittedCurve true to enable curve fitting, false to preserve raw paths
     * @return a new BezierTool instance
     */
    public static BezierTool createWithFittedCurves(BezierFigure prototype,
                                                    boolean calculateFittedCurve) {
        BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                .calculateFittedCurve(calculateFittedCurve)
                .build();
        return new BezierTool(config);
    }

    /**
     * Creates a BezierTool with full configuration control.
     * Use this when you need to specify all parameters explicitly.
     *
     * @param prototype The prototype figure used to create new BezierFigures
     * @param attributes Custom attributes to apply to created figures
     * @param presentationName Name displayed in undo operations
     * @param calculateFittedCurve true to enable curve fitting
     * @return a new BezierTool instance
     */
    public static BezierTool createFully(BezierFigure prototype,
                                         Map<AttributeKey<?>, Object> attributes,
                                         String presentationName,
                                         boolean calculateFittedCurve) {
        BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                .attributes(attributes)
                .presentationName(presentationName)
                .calculateFittedCurve(calculateFittedCurve)
                .build();
        return new BezierTool(config);
    }

    // Constructors

    /**
     * Primary constructor that accepts a configuration object.
     * This is the preferred way to create BezierTool instances.
     *
     * @param config The configuration object containing all initialization parameters
     */
    public BezierTool(BezierToolConfiguration config) {
        this.prototype = config.getPrototype();
        this.attributes = config.getAttributes();
        this.presentationName = config.getPresentationName();
        this.calculateFittedCurveAfterCreation = config.isCalculateFittedCurveAfterCreation();
    }

    // Legacy constructors maintained to preserve backward compatibility

    /**
     * Creates a new instance.
     * @deprecated Use {@link #createWithPrototype(BezierFigure)} instead
     */
    @Deprecated
    public BezierTool(BezierFigure prototype) {
        this(new BezierToolConfiguration.Builder(prototype).build());
    }

    /**
     * @deprecated Use {@link #createWithFittedCurves(BezierFigure, boolean)} instead
     */
    @Deprecated
    public BezierTool(BezierFigure prototype, boolean calculateFittedCurveAfterCreation) {
        this(new BezierToolConfiguration.Builder(prototype)
                .calculateFittedCurve(calculateFittedCurveAfterCreation)
                .build());
    }

    /**
     * Creates a new instance.
     * @deprecated Use {@link #createWithAttributes(BezierFigure, Map)} instead
     */
    @Deprecated
    public BezierTool(BezierFigure prototype, Map<AttributeKey<?>, Object> attributes) {
        this(new BezierToolConfiguration.Builder(prototype)
                .attributes(attributes)
                .build());
    }

    /**
     * @deprecated Use {@link #createFully(BezierFigure, Map, String, boolean)} instead
     */
    @Deprecated
    public BezierTool(BezierFigure prototype, Map<AttributeKey<?>, Object> attributes, String name) {
        this(new BezierToolConfiguration.Builder(prototype)
                .attributes(attributes)
                .presentationName(name)
                .build());
    }

    /**
     * @deprecated Use {@link #createFully(BezierFigure, Map, String, boolean)} instead
     */
    @Deprecated
    public BezierTool(BezierFigure prototype, Map<AttributeKey<?>, Object> attributes,
                      String name, boolean calculateFittedCurveAfterCreation) {
        this(new BezierToolConfiguration.Builder(prototype)
                .attributes(attributes)
                .presentationName(name)
                .calculateFittedCurve(calculateFittedCurveAfterCreation)
                .build());
    }

    // Public methods

    public String getPresentationName() {
        return presentationName;
    }

    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        getView().setCursor(Cursor.getDefaultCursor());
        if (createdFigure != null) {
            if (anchor != null && mouseLocation != null) {
                Rectangle r = new Rectangle(anchor);
                r.add(mouseLocation);
                if (createdFigure.getNodeCount() > 0 && createdFigure.isClosed()) {
                    r.add(getView().drawingToView(createdFigure.getStartPoint()));
                }
                fireAreaInvalidated(r);
            }
            finishCreation(createdFigure, creationView);
            createdFigure = null;
        }
    }

    // Handling of mouse events (Refactoring 3: Compose Method / Extract Method)

    /**
     * Handles mouse press events for creating Bezier paths.
     *
     * This method has been refactored using Compose Method (Kerievsky p. 123)
     * to improve readability. The idea is that the methods should be read as a sequence of steps
     * with implementation details in extracted helper methods.
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        logDebugIfEnabled("mousePressed", evt);

        invalidatePreviousMouseLocation(evt);
        mouseLocation = evt.getPoint();
        super.mousePressed(evt);

        handleViewChangeIfNeeded();

        if (createdFigure == null) {
            initializeNewFigure();
        } else {
            addNodeToExistingFigure(evt);
        }

        nodeCountBeforeDrag = createdFigure.getNodeCount();
    }

    /**
     * Logs debug information if debugging is enabled.
     */
    private void logDebugIfEnabled(String methodName, MouseEvent evt) {
        if (DEBUG) {
            System.out.println("BezierTool." + methodName + " " + evt);
        }
    }

    /**
     * Invalidates the area between the previous and current mouse locations
     * to ensure proper repainting of the rubber band line.
     */
    private void invalidatePreviousMouseLocation(MouseEvent evt) {
        if (mouseLocation != null) {
            Rectangle r = new Rectangle(mouseLocation);
            r.add(evt.getPoint());
            r.grow(1, 1);
            fireAreaInvalidated(r);
        }
    }

    /**
     * Handles the case when the user has switched to a different view
     * while creating a figure. Finishes the current figure before
     * starting in the new view.
     */
    private void handleViewChangeIfNeeded() {
        if (createdFigure != null && creationView != getView()) {
            finishCreation(createdFigure, creationView);
            createdFigure = null;
        }
    }

    /**
     * Initializes a new figure when the user starts drawing.
     * Sets up the creation view, creates the figure from the prototype,
     * adds the first node, and adds the figure to the drawing.
     */
    private void initializeNewFigure() {
        creationView = getView();
        creationView.clearSelection();
        finishWhenMouseReleased = null;

        createdFigure = createFigure();
        Point2D.Double constrainedPoint = getConstrainedPoint();
        createdFigure.addNode(new BezierPath.Node(constrainedPoint));
        getDrawing().add(createdFigure);
    }

    /**
     * Adds a node to the existing figure being created.
     * Only adds on single clicks to avoid duplicate nodes from double-clicks.
     */
    private void addNodeToExistingFigure(MouseEvent evt) {
        if (evt.getClickCount() == 1) {
            Point2D.Double constrainedPoint = getConstrainedPoint();
            addPointToFigure(constrainedPoint);
        }
    }

    /**
     * Gets the current anchor point, applying any active constrainer
     * (such as grid snapping) if one is configured.
     *
     * @return the constrained point in drawing coordinates
     */
    private Point2D.Double getConstrainedPoint() {
        Point2D.Double viewPoint = creationView.viewToDrawing(anchor);

        if (creationView.getConstrainer() == null) {
            return viewPoint;
        }

        return creationView.getConstrainer().constrainPoint(viewPoint, createdFigure);
    }

    @SuppressWarnings("unchecked")
    protected BezierFigure createFigure() {
        BezierFigure f = prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (attributes != null) {
            for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
                f.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
            }
        }
        return f;
    }

    protected Figure getCreatedFigure() {
        return createdFigure;
    }

    protected Figure getAddedFigure() {
        return createdFigure;
    }

    protected void addPointToFigure(Point2D.Double newPoint) {
        int pointCount = createdFigure.getNodeCount();
        createdFigure.willChange();
        if (pointCount < 2) {
            createdFigure.addNode(new BezierPath.Node(newPoint));
        } else {
            Point2D.Double endPoint = createdFigure.getEndPoint();
            Point2D.Double secondLastPoint = (pointCount <= 1) ? endPoint : createdFigure.getPoint(pointCount - 2, 0);
            if (newPoint.equals(endPoint)) {
                // nothing to do
            } else if (pointCount > 1 && Geom.lineContainsPoint(newPoint.x, newPoint.y, secondLastPoint.x, secondLastPoint.y, endPoint.x, endPoint.y, 0.9f / getView().getScaleFactor())) {
                createdFigure.setPoint(pointCount - 1, 0, newPoint);
            } else {
                createdFigure.addNode(new BezierPath.Node(newPoint));
            }
        }
        createdFigure.changed();
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (createdFigure != null) {
            switch (evt.getClickCount()) {
                case 1:
                    handleSingleClick(evt);
                    break;
                case 2:
                    handleDoubleClick();
                    break;
            }
        }
    }

    /**
     * Handles single click events.
     * Checks if the click is near the start point to close the path.
     */
    private void handleSingleClick(MouseEvent evt) {
        if (createdFigure.getNodeCount() > 2) {
            Rectangle r = new Rectangle(getView().drawingToView(createdFigure.getStartPoint()));
            r.grow(2, 2);
            if (r.contains(evt.getX(), evt.getY())) {
                createdFigure.setClosed(true);
                finishCreation(createdFigure, creationView);
                createdFigure = null;
                if (isToolDoneAfterCreation) {
                    fireToolDone();
                }
            }
        }
    }

    /**
     * Handles double click events.
     * Finishes the current figure creation.
     */
    private void handleDoubleClick() {
        finishWhenMouseReleased = null;
        finishCreation(createdFigure, creationView);
        createdFigure = null;
    }

    protected void fireUndoEvent(Figure createdFigure, DrawingView creationView) {
        final Figure addedFigure = createdFigure;
        final Drawing addedDrawing = creationView.getDrawing();
        final DrawingView addedView = creationView;
        getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                return presentationName;
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                addedDrawing.remove(addedFigure);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                addedView.clearSelection();
                addedDrawing.add(addedFigure);
                addedView.addToSelection(addedFigure);
            }
        });
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        logDebugIfEnabled("mouseReleased", evt);

        isWorking = false;

        applyCurveFittingIfNeeded();

        if (shouldFinishOnRelease()) {
            finishFigureOnRelease(evt);
            return;
        }

        updateFinishState();
        repaintRubberBand(evt);
    }

    /**
     * Applies Bezier curve fitting to the digitized path if enabled
     * and if enough points were added during the drag operation.
     */
    private void applyCurveFittingIfNeeded() {
        if (createdFigure.getNodeCount() > nodeCountBeforeDrag + 1) {
            createdFigure.willChange();
            BezierPath figurePath = createdFigure.getBezierPath();
            BezierPath digitizedPath = new BezierPath();
            for (int i = nodeCountBeforeDrag - 1, n = figurePath.size(); i < n; i++) {
                digitizedPath.add(figurePath.get(nodeCountBeforeDrag - 1));
                figurePath.remove(nodeCountBeforeDrag - 1);
            }
            BezierPath fittedPath = calculateFittedCurve(digitizedPath);
            figurePath.addAll(fittedPath);
            createdFigure.setBezierPath(figurePath);
            createdFigure.changed();
            nodeCountBeforeDrag = createdFigure.getNodeCount();
        }
    }

    /**
     * Determines if the figure should be finished when the mouse is released.
     */
    private boolean shouldFinishOnRelease() {
        return finishWhenMouseReleased == Boolean.TRUE && createdFigure.getNodeCount() > 1;
    }

    /**
     * Finishes figure creation on mouse release.
     */
    private void finishFigureOnRelease(MouseEvent evt) {
        Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
        r.add(evt.getX(), evt.getY());
        maybeFireBoundsInvalidated(r);
        finishCreation(createdFigure, creationView);
        createdFigure = null;
        finishWhenMouseReleased = null;
    }

    /**
     * Updates the finish state flag for subsequent mouse releases.
     */
    private void updateFinishState() {
        if (finishWhenMouseReleased == null) {
            finishWhenMouseReleased = Boolean.FALSE;
        }
    }

    /**
     * Repaints the rubber band line connecting the anchor to the mouse position.
     */
    private void repaintRubberBand(MouseEvent evt) {
        Rectangle r = new Rectangle(anchor);
        r.add(mouseLocation);
        r.add(evt.getPoint());
        r.grow(1, 1);
        fireAreaInvalidated(r);
        anchor.x = evt.getX();
        anchor.y = evt.getY();
        mouseLocation = evt.getPoint();
    }

    protected void finishCreation(BezierFigure createdFigure, DrawingView creationView) {
        fireUndoEvent(createdFigure, creationView);
        creationView.addToSelection(createdFigure);
        if (isToolDoneAfterCreation) {
            fireToolDone();
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        if (finishWhenMouseReleased == null) {
            finishWhenMouseReleased = Boolean.TRUE;
        }
        int x = evt.getX();
        int y = evt.getY();
        addPointToFigure(getView().viewToDrawing(new Point(x, y)));
    }

    @Override
    public void draw(Graphics2D g) {
        if (createdFigure != null
                && anchor != null
                && mouseLocation != null
                && getView() == creationView) {
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{1f, 5f}, 0f));
            g.drawLine(anchor.x, anchor.y, mouseLocation.x, mouseLocation.y);
            if (!isWorking && createdFigure.isClosed() && createdFigure.getNodeCount() > 1) {
                Point p = creationView.drawingToView(createdFigure.getStartPoint());
                g.drawLine(mouseLocation.x, mouseLocation.y, p.x, p.y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        if (createdFigure != null && anchor != null && mouseLocation != null) {
            if (creationView != null && evt.getSource() == creationView.getComponent()) {
                Rectangle r = new Rectangle(anchor);
                r.add(mouseLocation);
                r.add(evt.getPoint());
                if (createdFigure.isClosed() && createdFigure.getNodeCount() > 0) {
                    r.add(creationView.drawingToView(createdFigure.getStartPoint()));
                }
                r.grow(1, 1);
                fireAreaInvalidated(r);
                mouseLocation = evt.getPoint();
            }
        }
    }

    protected BezierPath calculateFittedCurve(BezierPath path) {
        if (calculateFittedCurveAfterCreation) {
            return Bezier.fitBezierPath(path, 1.5d / getView().getScaleFactor());
        } else {
            return path;
        }
    }

    public void setToolDoneAfterCreation(boolean b) {
        isToolDoneAfterCreation = b;
    }

    public boolean isToolDoneAfterCreation() {
        return isToolDoneAfterCreation;
    }
}
