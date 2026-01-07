package org.jhotdraw.samples.svg.gui;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.jhotdraw.draw.gui.JAttributeSlider;
import javax.swing.*;

public class GivenOpacitySliderTest extends Stage<GivenOpacitySliderTest> {

    @ProvidedScenarioState
    OpacitySliderState opacitySliderState = new OpacitySliderState();

    public GivenOpacitySliderTest a_opacity_slider() {
       opacitySliderState.slider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
       return this;
    }




}