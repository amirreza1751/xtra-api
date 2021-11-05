package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.SettingMapper;
import com.xtra.api.model.Setting;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.repository.SettingRepository;
import com.xtra.api.service.CrudService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingService extends CrudService<Setting, String, SettingRepository> {
    private final SettingMapper settingMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    protected SettingService(SettingRepository repository, SettingMapper settingMapper, ApplicationEventPublisher applicationEventPublisher) {
        super(repository, "Setting");
        this.settingMapper = settingMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected Page<Setting> findWithSearch(String search, Pageable page) {
        return null;
    }


    public Map<String, String> getSettings() {
        Map<String, String> settings = new HashMap<>();
        List<Setting> settingList  = repository.findAll();

        for (Setting setting:settingList) {
            settings.put(setting.getId(), setting.getValue());
        }
        return settings;
    }

    @Transactional
    public void updateSettingValues(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            var setting = repository.findById(key).orElseThrow(() -> new EntityNotFoundException("Setting"));
            setting.setValue(value);
            repository.save(setting);
        });
//        var backupSetting = settings.stream().filter(settingView -> settingView.getKey().equals("backup_interval")).findFirst();
//        backupSetting.ifPresent(settingView -> System.out.println("fired"));
//        backupSetting.ifPresent(settingView -> applicationEventPublisher.publishEvent(new SettingChangedEvent(settingView, SettingType.BACKUP)));
    }

    public SettingView getSetting(String key) {
        return settingMapper.toView(findByIdOrFail(key));
    }

    public String getSettingValue(String key) {
        return findByIdOrFail(key).getValue();
    }
}
