/*
 * @(#)CanvasToolBar.java
 *
 * Copyright (c) 2007-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.gui.plaf.palette.PaletteFormattedTextFieldUI;
import org.jhotdraw.gui.plaf.palette.PaletteButtonUI;
import org.jhotdraw.gui.plaf.palette.PaletteLabelUI;
import org.jhotdraw.gui.plaf.palette.PaletteSliderUI;
import org.jhotdraw.gui.plaf.palette.PaletteColorChooserUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.DefaultFormatterFactory;

import static org.jhotdraw.draw.AttributeKeys.CANVAS_HEIGHT;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_WIDTH;
import org.jhotdraw.draw.event.DrawingAttributeEditorHandler;
import org.jhotdraw.draw.event.DrawingComponentRepainter;
import org.jhotdraw.draw.gui.JAttributeSlider;
import org.jhotdraw.draw.gui.JAttributeTextField;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.text.ColorFormatter;
import org.jhotdraw.formatter.JavaNumberFormatter;
import org.jhotdraw.util.*;

/**
 * CanvasToolBar.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CanvasToolBar extends AbstractToolBar {

    private static final long serialVersionUID = 1L;
    private static final String LABELS_PATH = "org.jhotdraw.samples.svg.Labels";
    private static final String CANVAS_WIDTH_TOOL_TIP_TEXT = "attribute.canvasWidth.toolTipText";
    private static final String CANVAS_HEIGHT_TOOL_TIP_TEXT = "attribute.canvasHeight.toolTipText";
    private static final String CANVAS_FILL_COLOR = "attribute.canvasFillColor";
    private static final String CANVAS_FILL_OPACITY = "attribute.canvasFillOpacity";

    /**
     * Creates new instance.
     */
    public CanvasToolBar() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle(LABELS_PATH);
        setName(labels.getString(getID() + ".toolbar"));
        setDisclosureStateCount(3);
    }

    @Override
    protected JComponent createDisclosedComponent(int state) {
        JPanel p = null;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc;
        AbstractButton btn;
        JavaNumberFormatter formatter = new JavaNumberFormatter(1d, 4096d, 1d, true);
        JPopupButton opacityPopupButton;
        JAttributeSlider opacitySlider;
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle(LABELS_PATH);
        JLabel widthLabel;
        JLabel heightLabel;
        JAttributeTextField<Double> widthField;
        JAttributeTextField<Double> heightField;

        if (state == 1) {
            p = new JPanel();
            p.setLayout(layout);
            p.setOpaque(false);
            p.setBorder(new EmptyBorder(5, 5, 5, 8));
            // Abort if no editor is set
            if (editor == null) {
                return p;
            }
            // Fill color
            btn = ButtonFactory.createDrawingColorChooserButton(editor,
                    AttributeKeys.CANVAS_FILL_COLOR, CANVAS_FILL_COLOR, labels,
                    null, new Rectangle(3, 3, 10, 10), PaletteColorChooserUI.class, disposables);
            btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
            disposables.add(new DrawingComponentRepainter(editor, btn));
            ((JPopupButton) btn).setAction(null, null);
            gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p.add(btn, gbc);
            // Opacity slider
            opacityPopupButton = new JPopupButton();
            opacitySlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
            opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
            opacitySlider.setScaleFactor(100d);
            disposables.add(new DrawingAttributeEditorHandler<Double>(AttributeKeys.CANVAS_FILL_OPACITY, opacitySlider, editor));
            opacityPopupButton.add(opacitySlider);
            labels.configureToolBarButton(opacityPopupButton, CANVAS_FILL_OPACITY);
            opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
            opacityPopupButton.setIcon(
                    new DrawingOpacityIcon(editor, AttributeKeys.CANVAS_FILL_OPACITY, AttributeKeys.CANVAS_FILL_COLOR, null, Images.createImage(getClass(), labels.getString(CANVAS_FILL_OPACITY.concat(".icon"))),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
            disposables.add(new DrawingComponentRepainter(editor, opacityPopupButton));
            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.insets = new Insets(0, 3, 0, 0);
            p.add(opacityPopupButton, gbc);
            // Width and height fields
            widthLabel = new javax.swing.JLabel();
            heightLabel = new javax.swing.JLabel();
            widthField = new JAttributeTextField<Double>();
            heightField = new JAttributeTextField<Double>();
            widthLabel.setUI((LabelUI) PaletteLabelUI.createUI(widthLabel));
            widthLabel.setLabelFor(widthField);
            widthLabel.setToolTipText(labels.getString(CANVAS_WIDTH_TOOL_TIP_TEXT));
            widthLabel.setText(labels.getString("attribute.canvasWidth.text")); // NOI18N
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 0, 0, 0);
            p.add(widthLabel, gbc);
            widthField.setUI((TextUI) PaletteFormattedTextFieldUI.createUI(widthField));
            widthField.setColumns(3);
            widthField.setToolTipText(labels.getString(CANVAS_WIDTH_TOOL_TIP_TEXT));
            formatter.setUsesScientificNotation(false);
            widthField.setFormatterFactory(new DefaultFormatterFactory(formatter));
            widthField.setHorizontalAlignment(JTextField.LEADING);
            disposables.add(new DrawingAttributeEditorHandler<Double>(CANVAS_WIDTH, widthField, editor));
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 3, 0, 0);
            p.add(widthField, gbc);
            heightLabel.setUI((LabelUI) PaletteLabelUI.createUI(heightLabel));
            heightLabel.setLabelFor(widthField);
            heightLabel.setToolTipText(labels.getString(CANVAS_HEIGHT_TOOL_TIP_TEXT));
            heightLabel.setText(labels.getString("attribute.canvasHeight.text")); // NOI18N
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 0, 0, 0);
            p.add(heightLabel, gbc);
            heightField.setUI((TextUI) PaletteFormattedTextFieldUI.createUI(widthField));
            heightField.setColumns(3);
            heightField.setToolTipText(labels.getString(CANVAS_HEIGHT_TOOL_TIP_TEXT));
            formatter = new JavaNumberFormatter(1d, 4096d, 1d, true);
            formatter.setUsesScientificNotation(false);
            heightField.setFormatterFactory(new DefaultFormatterFactory(formatter));
            heightField.setHorizontalAlignment(JTextField.LEADING);
            disposables.add(new DrawingAttributeEditorHandler<Double>(CANVAS_HEIGHT, heightField, editor));
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 3, 0, 0);
            gbc.gridwidth = 2;
            p.add(heightField, gbc);
        }

        else if (state == 2) {
            p = new JPanel();
            p.setLayout(layout);
            p.setOpaque(false);
            // Abort if no editor is set
            if (editor == null) {
                return p;
            }
            JPanel p1 = new JPanel(new GridBagLayout());
            JPanel p2 = new JPanel(new GridBagLayout());
            JPanel p3 = new JPanel(new GridBagLayout());
            p1.setOpaque(false);
            p2.setOpaque(false);
            p3.setOpaque(false);
            p.removeAll();
            p.setBorder(new EmptyBorder(5, 5, 5, 8));
            layout = new GridBagLayout();
            p.setLayout(layout);
            // Fill color field with button
            JAttributeTextField<Color> colorField = new JAttributeTextField<Color>();
            colorField.setColumns(7);
            colorField.setToolTipText(labels.getString(CANVAS_FILL_COLOR.concat(".toolTipText")));
            colorField.putClientProperty("Palette.Component.segmentPosition", "first");
            colorField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(colorField));
            colorField.setFormatterFactory(ColorFormatter.createFormatterFactory());
            colorField.setHorizontalAlignment(JTextField.LEFT);
            disposables.add(new DrawingAttributeEditorHandler<Color>(AttributeKeys.CANVAS_FILL_COLOR, colorField, editor));
            gbc = new GridBagConstraints();
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p1.add(colorField, gbc);
            btn = ButtonFactory.createDrawingColorChooserButton(editor,
                    AttributeKeys.CANVAS_FILL_COLOR, CANVAS_FILL_COLOR, labels,
                    null, new Rectangle(3, 3, 10, 10), PaletteColorChooserUI.class, disposables);
            btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
            disposables.add(new DrawingComponentRepainter(editor, btn));
            ((JPopupButton) btn).setAction(null, null);
            gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p1.add(btn, gbc);
            // Opacity field with slider
            JAttributeTextField<Double> opacityField = new JAttributeTextField<Double>();
            opacityField.setColumns(4);
            opacityField.setToolTipText(labels.getString("attribute.figureOpacity.toolTipText"));
            opacityField.setHorizontalAlignment(JAttributeTextField.RIGHT);
            opacityField.putClientProperty("Palette.Component.segmentPosition", "first");
            opacityField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(opacityField));
            formatter = new JavaNumberFormatter(0d, 100d, 100d, false, "%");
            formatter.setUsesScientificNotation(false);
            formatter.setMaximumFractionDigits(1);
            opacityField.setFormatterFactory(new DefaultFormatterFactory(formatter));
            opacityField.setHorizontalAlignment(JTextField.LEADING);
            disposables.add(new DrawingAttributeEditorHandler<Double>(AttributeKeys.CANVAS_FILL_OPACITY, opacityField, editor));
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(3, 0, 0, 0);
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p1.add(opacityField, gbc);
            opacityPopupButton = new JPopupButton();
            opacitySlider = new JAttributeSlider(JSlider.VERTICAL, 0, 100, 100);
            opacitySlider.setUI((SliderUI) PaletteSliderUI.createUI(opacitySlider));
            opacitySlider.setScaleFactor(100d);
            disposables.add(new DrawingAttributeEditorHandler<Double>(AttributeKeys.CANVAS_FILL_OPACITY, opacitySlider, editor));
            opacityPopupButton.add(opacitySlider);
            labels.configureToolBarButton(opacityPopupButton, CANVAS_FILL_OPACITY);
            opacityPopupButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(opacityPopupButton));
            opacityPopupButton.setIcon(
                    new DrawingOpacityIcon(editor, AttributeKeys.CANVAS_FILL_OPACITY, AttributeKeys.CANVAS_FILL_COLOR, null, Images.createImage(getClass(), labels.getString(CANVAS_FILL_OPACITY.concat(".icon"))),
                            new Rectangle(5, 5, 6, 6), new Rectangle(4, 4, 7, 7)));
            disposables.add(new DrawingComponentRepainter(editor, opacityPopupButton));
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.insets = new Insets(3, 0, 0, 0);
            p1.add(opacityPopupButton, gbc);
            // Width and height fields
            widthLabel = new javax.swing.JLabel();
            heightLabel = new javax.swing.JLabel();
            widthField = new JAttributeTextField<Double>();
            heightField = new JAttributeTextField<Double>();
            widthLabel.setUI((LabelUI) PaletteLabelUI.createUI(widthLabel));
            widthLabel.setLabelFor(widthField);
            widthLabel.setToolTipText(labels.getString(CANVAS_WIDTH_TOOL_TIP_TEXT));
            widthLabel.setText(labels.getString("attribute.canvasWidth.text")); // NOI18N
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 0, 0, 0);
            p3.add(widthLabel, gbc);
            widthField.setUI((TextUI) PaletteFormattedTextFieldUI.createUI(widthField));
            widthField.setColumns(3);
            widthField.setToolTipText(labels.getString(CANVAS_WIDTH_TOOL_TIP_TEXT));
            widthField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(1d, 4096d, 1d, true));
            widthField.setHorizontalAlignment(JTextField.LEADING);
            disposables.add(new DrawingAttributeEditorHandler<Double>(CANVAS_WIDTH, widthField, editor));
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 3, 0, 0);
            p3.add(widthField, gbc);
            heightLabel.setUI((LabelUI) PaletteLabelUI.createUI(heightLabel));
            heightLabel.setLabelFor(widthField);
            heightLabel.setToolTipText(labels.getString(CANVAS_HEIGHT_TOOL_TIP_TEXT));
            heightLabel.setText(labels.getString("attribute.canvasHeight.text")); // NOI18N
            gbc = new GridBagConstraints();
            gbc.gridx = 3;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 3, 0, 0);
            p3.add(heightLabel, gbc);
            heightField.setUI((TextUI) PaletteFormattedTextFieldUI.createUI(widthField));
            heightField.setColumns(3);
            heightField.setToolTipText(labels.getString(CANVAS_HEIGHT_TOOL_TIP_TEXT));
            heightField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(1d, 4096d, 1d, true));
            heightField.setHorizontalAlignment(JTextField.LEADING);
            disposables.add(new DrawingAttributeEditorHandler<Double>(CANVAS_HEIGHT, heightField, editor));
            gbc = new GridBagConstraints();
            gbc.gridx = 4;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 3, 0, 0);
            p3.add(heightField, gbc);
            // Add horizontal strips
            gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p.add(p1, gbc);
            gbc = new GridBagConstraints();
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p.add(p2, gbc);
            gbc = new GridBagConstraints();
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            p.add(p3, gbc);
        } else {
            return p;
        }
        return p;
    }

    @Override
    protected String getID() {
        return "canvas";
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setOpaque(false);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
