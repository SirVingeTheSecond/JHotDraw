package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import static org.junit.Assert.*;

public class ThenOpacitySliderTest extends Stage<ThenOpacitySliderTest> {

    @ProvidedScenarioState
    OpacitySliderState sliderState;

    public ThenOpacitySliderTest the_value_is (int expectedValue) {
        assertEquals(expectedValue, sliderState.slider.getValue());
        return this;
    }

}