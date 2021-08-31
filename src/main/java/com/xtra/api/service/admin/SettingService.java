package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.SettingMapper;
import com.xtra.api.model.Setting;
import com.xtra.api.model.event.SettingChangedEvent;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.setting.SettingType;
import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.repository.SettingRepository;
import com.xtra.api.service.CrudService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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


    public List<SettingView> getSettings(List<String> settingKeys) {
        return repository.findAllById(settingKeys).stream().map(settingMapper::toView).collect(Collectors.toList());
    }

    @Transactional
    public void updateSettingValues(List<SettingView> settings) {
        settings.forEach(s -> {
            var setting = repository.findById(s.getKey()).orElseThrow(() -> new EntityNotFoundException("Setting"));
            setting.setValue(s.getValue());
            repository.save(setting);
        });
        var backupSetting = settings.stream().filter(settingView -> settingView.getKey().equals("backup_interval")).findFirst();
        backupSetting.ifPresent(settingView -> System.out.println("fired"));
        backupSetting.ifPresent(settingView -> applicationEventPublisher.publishEvent(new SettingChangedEvent(settingView, SettingType.BACKUP)));
    }
}
