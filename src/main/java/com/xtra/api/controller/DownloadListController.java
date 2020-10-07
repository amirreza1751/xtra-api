package com.xtra.api.controller;

import com.xtra.api.facade.DownloadListFacade;
import com.xtra.api.mapper.DlCollectionMapper;
import com.xtra.api.mapper.DownloadListMapper;
import com.xtra.api.projection.DownloadListDto;
import com.xtra.api.service.DownloadListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("download_lists")
public class DownloadListController {
    private final DownloadListService dlService;
    private final DownloadListFacade facade;
    private final DownloadListMapper mapper;
    private final DlCollectionMapper rMapper;

    @Autowired
    public DownloadListController(DownloadListService downloadListService, DownloadListFacade downloadListFacade, DownloadListMapper mapper, DlCollectionMapper rMapper) {
        this.dlService = downloadListService;
        this.facade = downloadListFacade;
        this.mapper = mapper;
        this.rMapper = rMapper;
    }

    @GetMapping("")
    public ResponseEntity<Page<DownloadListDto>> getDownloadLists(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                  @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = dlService.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(new PageImpl<>(result.stream().map(facade::convertToDto).collect(Collectors.toList())));
    }

    @GetMapping("/default")
    public DownloadListDto getSystemDefaultDownloadList() {
        return facade.convertToDto(dlService.getDefaultDownloadList());
    }

    @GetMapping("/user/{userId}")
    public List<DownloadListDto> getDownloadListsByUserId(@PathVariable Long userId) {
        return dlService.getDownloadListsByUserId(userId).stream().map(facade::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DownloadListDto> getDownloadList(@PathVariable Long id) {
        var res = dlService.findByIdOrFail(id);
        return ResponseEntity.ok(mapper.convertToDto(res));
    }

    @PostMapping("")
    public ResponseEntity<DownloadListDto> addDownloadList(@RequestBody DownloadListDto downloadListDto) {
        var entity = mapper.convertToEntity(downloadListDto);
        var result = dlService.add(entity);
        dlService.saveRelationship(result, rMapper.convertAllToEntity(downloadListDto.getCollections()));
        return ResponseEntity.ok(mapper.convertToDto(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DownloadListDto> updateDownloadList(@PathVariable Long id, @RequestBody @Valid DownloadListDto downloadListDto) {
        return ResponseEntity.ok(facade.convertToDto(dlService.updateOrFail(id, facade.convertToEntity(downloadListDto))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDownloadList(@PathVariable Long id) {
        dlService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
