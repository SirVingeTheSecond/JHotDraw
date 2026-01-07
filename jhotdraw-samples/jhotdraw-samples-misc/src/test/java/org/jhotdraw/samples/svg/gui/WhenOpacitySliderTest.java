package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class WhenOpacitySliderTest extends Stage<WhenOpacitySliderTest> {

    @ProvidedScenarioState
    OpacitySliderState sliderState;

    public WhenOpacitySliderTest the_slider_is_set_to (int wantedValue) {
        sliderState.slider.setValue(wantedValue);
        return this;
    }

}