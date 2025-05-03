package events;

import types.Event;
import types.Panel;
import types.enums.EventType;

public class UserSettingsEvent extends Event {
    private final Panel settings;

    public UserSettingsEvent(Panel settings) {
        super(EventType.USER_SETTINGS);
        this.settings = settings;
    }

    public Panel getSettings() {
        return settings;
    }    
}
