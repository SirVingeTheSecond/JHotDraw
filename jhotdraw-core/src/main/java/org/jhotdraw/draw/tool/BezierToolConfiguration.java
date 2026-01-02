package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.figure.BezierFigure;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.util.ResourceBundleUtil;
import java.util.Map;

/**
 * Configuration object for BezierTool that encapsulates all initialization parameters.
 *
 * This class was introduced through the "Introduce Parameter Object" refactoring
 * (Fowler p. 295) to address the Long Parameter List smell in BezierTool constructors.
 *
 * The Builder pattern handles optional parameters elegantly, allowing callers to
 * specify only what they REALLY need.
 *
 * @author Refactored for Software Maintenance Portfolio
 */
public class BezierToolConfiguration {

    private final BezierFigure prototype;
    private final Map<AttributeKey<?>, Object> attributes;
    private final String presentationName;
    private final boolean calculateFittedCurveAfterCreation;

    /**
     * Private constructor - use Builder to create instances.
     */
    private BezierToolConfiguration(Builder builder) {
        this.prototype = builder.prototype;
        this.attributes = builder.attributes;
        this.presentationName = builder.presentationName;
        this.calculateFittedCurveAfterCreation = builder.calculateFittedCurveAfterCreation;
    }

    public BezierFigure getPrototype() {
        return prototype;
    }

    public Map<AttributeKey<?>, Object> getAttributes() {
        return attributes;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public boolean isCalculateFittedCurveAfterCreation() {
        return calculateFittedCurveAfterCreation;
    }

    /**
     * Builder for BezierToolConfiguration.
     *
     * The prototype parameter is required and must be provided to the Builder constructor.
     * All other parameters are optional and have sensible defaults.
     */
    public static class Builder {
        // Required parameter
        private final BezierFigure prototype;

        // Optional parameters with defaults
        private Map<AttributeKey<?>, Object> attributes = null;
        private String presentationName = null;
        private boolean calculateFittedCurveAfterCreation = true;

        /**
         * Creates a new Builder with the required prototype parameter.
         *
         * @param prototype The prototype figure used to create new BezierFigures.
         *                  Must not be null.
         * @throws IllegalArgumentException if prototype is null
         */
        public Builder(BezierFigure prototype) {
            if (prototype == null) {
                throw new IllegalArgumentException("Prototype must not be null");
            }
            this.prototype = prototype;
        }

        /**
         * Sets the attributes to apply to created figures.
         *
         * @param attributes Map of attribute keys to values, or null for no custom attributes
         * @return this Builder for method chaining
         */
        public Builder attributes(Map<AttributeKey<?>, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        /**
         * Sets the presentation name displayed in undo operations.
         *
         * @param name The presentation name, or null to use default from resource bundle
         * @return this Builder for method chaining
         */
        public Builder presentationName(String name) {
            this.presentationName = name;
            return this;
        }

        /**
         * Sets whether to calculate fitted curves after creation.
         *
         * When true, the tool will smooth freehand paths using Bezier curve fitting.
         * When false, the raw digitized path is preserved.
         *
         * @param calculate true to enable curve fitting, false to preserve raw paths
         * @return this Builder for method chaining
         */
        public Builder calculateFittedCurve(boolean calculate) {
            this.calculateFittedCurveAfterCreation = calculate;
            return this;
        }

        /**
         * Builds the BezierToolConfiguration instance.
         *
         * If no presentation name was specified, the default is loaded from
         * the resource bundle.
         *
         * @return a new BezierToolConfiguration instance
         */
        public BezierToolConfiguration build() {
            if (presentationName == null) {
                ResourceBundleUtil labels =
                        ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
                presentationName = labels.getString("edit.createFigure.text");
            }
            return new BezierToolConfiguration(this);
        }
    }
}
