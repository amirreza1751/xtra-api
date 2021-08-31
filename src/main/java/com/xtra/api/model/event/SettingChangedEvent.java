package com.xtra.api.model.event;

import com.xtra.api.model.setting.SettingType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SettingChangedEvent extends ApplicationEvent {
    private final SettingType settingType;

    public SettingChangedEvent(Object source, SettingType settingType) {
        super(source);
        this.settingType = settingType;
    }
}
