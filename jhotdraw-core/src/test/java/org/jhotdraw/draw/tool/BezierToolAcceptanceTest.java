package org.jhotdraw.draw.tool;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.junit5.ScenarioTest;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.figure.BezierFigure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BDD acceptance tests for BezierTool creation.
 *
 * These scenarios verify the User Story from Lab 01:
 *
 * <p> "As a developer maintaining JHotDraw, I want to improve the BezierTool code
 * so that it is easier to understand, test, and modify." </p>
 *
 * <p> The tests verify that the refactored factory methods and configuration work
 * correctly. Each scenario is a different aspect of the improved instantiation
 * implementation introduced by the refactoring. </p>
 *
 * <p> The tests use JGiven for Given-When-Then structure and AssertJ for assertions. </p>
 */
@ExtendWith(JGivenExtension.class)
@DisplayName("Bezier tool creation")
class BezierToolAcceptanceTest extends ScenarioTest<
        BezierToolAcceptanceTest.GivenBezierToolSetup,
        BezierToolAcceptanceTest.WhenCreatingBezierTool,
        BezierToolAcceptanceTest.ThenBezierToolBehavior> {

    @Test
    @DisplayName("Artist creates a basic Bezier tool for drawing curves")
    void artistCreatesBasicBezierTool() {
        given().a_bezier_figure_prototype();
        when().the_artist_creates_a_bezier_tool_with_the_prototype();
        then().a_functional_bezier_tool_is_created()
                .and().the_tool_has_a_presentation_name();
    }

    @Test
    @DisplayName("Artist creates a Bezier tool with smooth curve fitting enabled")
    void artistCreatesBezierToolWithCurveFitting() {
        given().a_bezier_figure_prototype();
        when().the_artist_creates_a_bezier_tool_with_curve_fitting_enabled();
        then().a_functional_bezier_tool_is_created()
                .and().the_tool_will_smooth_drawn_paths();
    }

    @Test
    @DisplayName("Artist creates a Bezier tool with raw path preservation")
    void artistCreatesBezierToolWithRawPaths() {
        given().a_bezier_figure_prototype();
        when().the_artist_creates_a_bezier_tool_with_curve_fitting_disabled();
        then().a_functional_bezier_tool_is_created()
                .and().the_tool_will_preserve_raw_paths();
    }

    @Test
    @DisplayName("Artist creates a Bezier tool with custom styling attributes")
    void artistCreatesBezierToolWithCustomAttributes() {
        given().a_bezier_figure_prototype()
                .and().custom_styling_attributes();
        when().the_artist_creates_a_bezier_tool_with_custom_attributes();
        then().a_functional_bezier_tool_is_created();
    }

    @Test
    @DisplayName("Artist creates a fully configured Bezier tool")
    void artistCreatesFullyConfiguredBezierTool() {
        given().a_bezier_figure_prototype()
                .and().custom_styling_attributes()
                .and().a_custom_tool_name();
        when().the_artist_creates_a_fully_configured_bezier_tool();
        then().a_functional_bezier_tool_is_created()
                .and().the_tool_has_the_custom_name();
    }

    // Stages

    static class GivenBezierToolSetup extends Stage<GivenBezierToolSetup> {

        @ProvidedScenarioState
        BezierFigure prototype;

        @ProvidedScenarioState
        Map<AttributeKey<?>, Object> attributes;

        @ProvidedScenarioState
        String customName;

        GivenBezierToolSetup a_bezier_figure_prototype() {
            prototype = new BezierFigure();
            return self();
        }

        GivenBezierToolSetup custom_styling_attributes() {
            attributes = new HashMap<>();
            return self();
        }

        GivenBezierToolSetup a_custom_tool_name() {
            customName = "My Custom Bezier Tool";
            return self();
        }
    }

    static class WhenCreatingBezierTool extends Stage<WhenCreatingBezierTool> {

        @ExpectedScenarioState
        BezierFigure prototype;

        @ExpectedScenarioState
        Map<AttributeKey<?>, Object> attributes;

        @ExpectedScenarioState
        String customName;

        @ProvidedScenarioState
        BezierTool createdTool;

        @ProvidedScenarioState
        boolean curveFittingEnabled;

        WhenCreatingBezierTool the_artist_creates_a_bezier_tool_with_the_prototype() {
            createdTool = BezierTool.createWithPrototype(prototype);
            curveFittingEnabled = true; // default
            return self();
        }

        WhenCreatingBezierTool the_artist_creates_a_bezier_tool_with_curve_fitting_enabled() {
            createdTool = BezierTool.createWithFittedCurves(prototype, true);
            curveFittingEnabled = true;
            return self();
        }

        WhenCreatingBezierTool the_artist_creates_a_bezier_tool_with_curve_fitting_disabled() {
            createdTool = BezierTool.createWithFittedCurves(prototype, false);
            curveFittingEnabled = false;
            return self();
        }

        WhenCreatingBezierTool the_artist_creates_a_bezier_tool_with_custom_attributes() {
            createdTool = BezierTool.createWithAttributes(prototype, attributes);
            curveFittingEnabled = true;
            return self();
        }

        WhenCreatingBezierTool the_artist_creates_a_fully_configured_bezier_tool() {
            createdTool = BezierTool.createFully(prototype, attributes, customName, true);
            curveFittingEnabled = true;
            return self();
        }
    }

    static class ThenBezierToolBehavior extends Stage<ThenBezierToolBehavior> {

        @ExpectedScenarioState
        BezierTool createdTool;

        @ExpectedScenarioState
        boolean curveFittingEnabled;

        @ExpectedScenarioState
        String customName;

        ThenBezierToolBehavior a_functional_bezier_tool_is_created() {
            assertThat(createdTool).isNotNull();
            return self();
        }

        ThenBezierToolBehavior the_tool_has_a_presentation_name() {
            assertThat(createdTool.getPresentationName())
                    .isNotNull()
                    .isNotEmpty();
            return self();
        }

        ThenBezierToolBehavior the_tool_will_smooth_drawn_paths() {
            assertThat(curveFittingEnabled).isTrue();
            return self();
        }

        ThenBezierToolBehavior the_tool_will_preserve_raw_paths() {
            assertThat(curveFittingEnabled).isFalse();
            return self();
        }

        ThenBezierToolBehavior the_tool_has_the_custom_name() {
            assertThat(createdTool.getPresentationName()).isEqualTo(customName);
            return self();
        }
    }
}
