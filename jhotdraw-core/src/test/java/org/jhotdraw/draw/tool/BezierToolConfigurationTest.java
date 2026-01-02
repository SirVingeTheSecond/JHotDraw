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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for BezierToolConfiguration.
 *
 * These tests verify the Builder pattern implementation and configuration
 * object behavior introduced by the Parameter Object refactoring.
 */
@DisplayName("BezierToolConfiguration")
class BezierToolConfigurationTest {

    private BezierFigure prototype;

    @BeforeEach
    void setUp() {
        prototype = new BezierFigure();
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("should require non-null prototype")
        @SuppressWarnings("DataFlowIssue")
        // will always throw an exception because the Builder constructor rejects null which is why IntelliJ sees it as a warning
        void shouldRequireNonNullPrototype() {
            assertThatThrownBy(() -> new BezierToolConfiguration.Builder(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Prototype must not be null");
        }

        @Test
        @DisplayName("should create configuration with only prototype")
        void shouldCreateConfigurationWithOnlyPrototype() {
            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .build();

            // Then
            assertThat(config.getPrototype()).isSameAs(prototype);
            assertThat(config.getAttributes()).isNull();
            assertThat(config.isCalculateFittedCurveAfterCreation()).isTrue();
            assertThat(config.getPresentationName()).isNotNull();
        }

        @Test
        @DisplayName("should apply custom attributes")
        void shouldApplyCustomAttributes() {
            // Given
            Map<AttributeKey<?>, Object> attributes = new HashMap<>();

            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .attributes(attributes)
                    .build();

            // Then
            assertThat(config.getAttributes()).isSameAs(attributes);
        }

        @Test
        @DisplayName("should apply custom presentation name")
        void shouldApplyCustomPresentationName() {
            // Given
            String customName = "Custom Tool Name";

            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .presentationName(customName)
                    .build();

            // Then
            assertThat(config.getPresentationName()).isEqualTo(customName);
        }

        @Test
        @DisplayName("should disable curve fitting when specified")
        void shouldDisableCurveFittingWhenSpecified() {
            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .calculateFittedCurve(false)
                    .build();

            // Then
            assertThat(config.isCalculateFittedCurveAfterCreation()).isFalse();
        }

        @Test
        @DisplayName("should support fluent chaining of all options")
        void shouldSupportFluentChainingOfAllOptions() {
            // Given
            Map<AttributeKey<?>, Object> attributes = new HashMap<>();
            String name = "Full Configuration";

            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .attributes(attributes)
                    .presentationName(name)
                    .calculateFittedCurve(false)
                    .build();

            // Then
            assertThat(config.getPrototype()).isSameAs(prototype);
            assertThat(config.getAttributes()).isSameAs(attributes);
            assertThat(config.getPresentationName()).isEqualTo(name);
            assertThat(config.isCalculateFittedCurveAfterCreation()).isFalse();
        }
    }


    @Nested
    @DisplayName("Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("should return same values on repeated calls")
        void shouldReturnSameValuesOnRepeatedCalls() {
            // Given
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .presentationName("Test")
                    .calculateFittedCurve(true)
                    .build();

            // When
            BezierFigure firstPrototypeCall = config.getPrototype();
            BezierFigure secondPrototypeCall = config.getPrototype();
            String firstName = config.getPresentationName();
            String secondName = config.getPresentationName();
            boolean firstCurveFitting = config.isCalculateFittedCurveAfterCreation();
            boolean secondCurveFitting = config.isCalculateFittedCurveAfterCreation();

            // Then
            assertThat(firstPrototypeCall).isSameAs(secondPrototypeCall);
            assertThat(firstName).isEqualTo(secondName);
            assertThat(firstCurveFitting).isEqualTo(secondCurveFitting);
        }
    }

    @Nested
    @DisplayName("Default Values")
    class DefaultValueTests {

        @Test
        @DisplayName("should default curve fitting to true")
        void shouldDefaultCurveFittingToTrue() {
            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .build();

            // Then
            assertThat(config.isCalculateFittedCurveAfterCreation()).isTrue();
        }

        @Test
        @DisplayName("should default attributes to null")
        void shouldDefaultAttributesToNull() {
            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .build();

            // Then
            assertThat(config.getAttributes()).isNull();
        }

        @Test
        @DisplayName("should provide default presentation name from resource bundle")
        void shouldProvideDefaultPresentationNameFromResourceBundle() {
            // When
            BezierToolConfiguration config = new BezierToolConfiguration.Builder(prototype)
                    .build();

            // Then
            assertThat(config.getPresentationName()).isNotNull().isNotEmpty();
        }
    }
}
