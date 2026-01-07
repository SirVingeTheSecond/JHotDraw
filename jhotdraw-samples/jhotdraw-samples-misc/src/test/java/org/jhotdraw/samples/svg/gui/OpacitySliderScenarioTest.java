package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

public class OpacitySliderScenarioTest extends ScenarioTest<GivenOpacitySliderTest,
        WhenOpacitySliderTest,
        ThenOpacitySliderTest> {

    @Test
    public void opacity_slider_returns_value() {
        given().a_opacity_slider();
        when().the_slider_is_set_to(30);
        then().the_value_is(30);
    }
}