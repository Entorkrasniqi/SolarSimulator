package events.handlers;

import model.Calculator;
import types.Panel;
import types.Event;
import types.EventHandler;

import events.UserSettingsEvent;

public class UserSettingsEventHandler implements EventHandler<UserSettingsEvent> {
    private Panel currentSettings;
    private final Calculator calculator;

    public UserSettingsEventHandler(Calculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public void handle(UserSettingsEvent event) {
        currentSettings = event.getSettings();

        calculator.updateSettings(currentSettings);

        // print settings updated!!
        System.out.println("Settings updated:");
        System.out.println(currentSettings);
    }
    
    public Panel getCurrentSettings() {
        return currentSettings;
    }
}
