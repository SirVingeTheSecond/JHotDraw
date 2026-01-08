package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.figure.BezierFigure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BezierTool factory methods.
 *
 * These tests verify the Replace Constructors with Creation Methods refactoring
 * by testing each factory method produces correctly configured instances.
 */
@DisplayName("BezierTool Factory Methods")
class BezierToolFactoryTest {

    private BezierFigure prototype;

    @BeforeEach
    void setUp() {
        prototype = new BezierFigure();
    }

    @Nested
    @DisplayName("createWithPrototype")
    class CreateWithPrototypeTests {

        @Test
        @DisplayName("should create tool with default settings")
        void shouldCreateToolWithDefaultSettings() {
            // When
            BezierTool tool = BezierTool.createWithPrototype(prototype);

            // Then
            assertThat(tool).isNotNull();
            assertThat(tool.getPresentationName()).isNotNull();
        }

        @Test
        @DisplayName("should create independent instances")
        void shouldCreateIndependentInstances() {
            // When
            BezierTool tool1 = BezierTool.createWithPrototype(prototype);
            BezierTool tool2 = BezierTool.createWithPrototype(prototype);

            // Then
            assertThat(tool1).isNotSameAs(tool2);
        }
    }

    @Nested
    @DisplayName("createWithAttributes")
    class CreateWithAttributesTests {

        @Test
        @DisplayName("should create tool with custom attributes")
        void shouldCreateToolWithCustomAttributes() {
            // Given
            Map<AttributeKey<?>, Object> attributes = new HashMap<>();

            // When
            BezierTool tool = BezierTool.createWithAttributes(prototype, attributes);

            // Then
            assertThat(tool).isNotNull();
        }

        @Test
        @DisplayName("should accept null attributes")
        void shouldAcceptNullAttributes() {
            // When
            BezierTool tool = BezierTool.createWithAttributes(prototype, null);

            // Then
            assertThat(tool).isNotNull();
        }
    }

    @Nested
    @DisplayName("createWithFittedCurves")
    class CreateWithFittedCurvesTests {

        @Test
        @DisplayName("should create tool with curve fitting enabled")
        void shouldCreateToolWithCurveFittingEnabled() {
            // When
            BezierTool tool = BezierTool.createWithFittedCurves(prototype, true);

            // Then
            assertThat(tool).isNotNull();
        }

        @Test
        @DisplayName("should create tool with curve fitting disabled")
        void shouldCreateToolWithCurveFittingDisabled() {
            // When
            BezierTool tool = BezierTool.createWithFittedCurves(prototype, false);

            // Then
            assertThat(tool).isNotNull();
        }
    }

    @Nested
    @DisplayName("createFully")
    class CreateFullyTests {

        @Test
        @DisplayName("should create tool with all parameters specified")
        void shouldCreateToolWithAllParametersSpecified() {
            // Given
            Map<AttributeKey<?>, Object> attributes = new HashMap<>();
            String name = "Custom Bezier Tool";

            // When
            BezierTool tool = BezierTool.createFully(prototype, attributes, name, false);

            // Then
            assertThat(tool).isNotNull();
            assertThat(tool.getPresentationName()).isEqualTo(name);
        }

        @Test
        @DisplayName("should handle null optional parameters")
        void shouldHandleNullOptionalParameters() {
            // When
            BezierTool tool = BezierTool.createFully(prototype, null, null, true);

            // Then
            assertThat(tool).isNotNull();
            assertThat(tool.getPresentationName()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Legacy Constructor Compatibility")
    class LegacyConstructorTests {

        @Test
        @DisplayName("should support legacy single-argument constructor")
        void shouldSupportLegacySingleArgumentConstructor() {
            // When
            @SuppressWarnings("deprecation")
            BezierTool tool = new BezierTool(prototype);

            // Then
            assertThat(tool).isNotNull();
        }

        @Test
        @DisplayName("should support legacy two-argument constructor with boolean")
        void shouldSupportLegacyTwoArgumentConstructorWithBoolean() {
            // When
            @SuppressWarnings("deprecation")
            BezierTool tool = new BezierTool(prototype, false);

            // Then
            assertThat(tool).isNotNull();
        }

        @Test
        @DisplayName("should support legacy two-argument constructor with attributes")
        void shouldSupportLegacyTwoArgumentConstructorWithAttributes() {
            // Given
            Map<AttributeKey<?>, Object> attributes = new HashMap<>();

            // When
            @SuppressWarnings("deprecation")
            BezierTool tool = new BezierTool(prototype, attributes);

            // Then
            assertThat(tool).isNotNull();
        }

        @Test
        @DisplayName("should support legacy four-argument constructor")
        void shouldSupportLegacyFourArgumentConstructor() {
            // Given
            Map<AttributeKey<?>, Object> attributes = new HashMap<>();

            // When
            @SuppressWarnings("deprecation")
            BezierTool tool = new BezierTool(prototype, attributes, "Test", true);

            // Then
            assertThat(tool).isNotNull();
            assertThat(tool.getPresentationName()).isEqualTo("Test");
        }
    }
}
