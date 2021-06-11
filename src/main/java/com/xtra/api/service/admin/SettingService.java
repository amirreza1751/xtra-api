package com.xtra.api.service.admin;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.SettingMapper;
import com.xtra.api.model.Setting;
import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.repository.SettingRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettingService extends CrudService<Setting, String, SettingRepository> {
    private final SettingMapper settingMapper;

    protected SettingService(SettingRepository repository, SettingMapper settingMapper) {
        super(repository, "Setting");
        this.settingMapper = settingMapper;
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
    }
}
