package com.schemainsight.userinterface;

import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CustomButton extends Button {

    public CustomButton(String text) {
        super(text);
    }

    public static CustomButton createTopButton(String text) {
        CustomButton button = new CustomButton(text);
        button.getStyleClass().add("top-button");
        return button;
    }

    public static CustomButton createSidebarButton(String text, String tooltip, EventHandler<ActionEvent> eventHandler) {
        CustomButton button = new CustomButton(text);
        button.getStyleClass().add("sidebar-button");

        if (tooltip != null) {
            button.setTooltip(new Tooltip(tooltip));
        }

        button.setOnAction(eventHandler);
        return button;
    }
}
