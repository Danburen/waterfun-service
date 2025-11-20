package org.waterwood.waterfunservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.waterwood.waterfunservice.dto.common.ServiceResult;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.dto.response.MiniFileResData;
import org.waterwood.waterfunservice.service.resource.ResourceService;
import org.waterwood.waterfunservice.service.resource.LegalResourceConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/resource/")
@Slf4j
public class ResourceController {
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("legal/{type}/{lang}/{fileName}")
    public ResponseEntity<?> getLegalResource(
            @PathVariable String type,
            @PathVariable String lang,
            @PathVariable String fileName) {
        // White list validateAndRemove path
        if (! LegalResourceConstants.VALID_TYPES.contains(type)
                || !LegalResourceConstants.VALID_LANGS.contains(lang)) {
            return ResponseCode.REQUEST_NOT_IN_WHITELIST.toResponseEntity();
        }
        // Standardized path
        if (!isSafePathSegment(type) || !isSafePathSegment(lang) || !isSafePathSegment(fileName)) {
            return ResponseCode.INVALID_PATH.toResponseEntity();
        }
        try {
            ServiceResult<MiniFileResData> res = resourceService.getLegalFileContent(type, lang, fileName);
            if(! res.isSuccess()) return res.toResponseEntity();

            MiniFileResData data = res.getData();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isSafePathSegment(String segment) {
        return segment != null && segment.matches("^[a-zA-Z0-9_\\-.]++$");
    }

    private String detectContentType(String fileName) throws IOException {
        return Optional.ofNullable(Files.probeContentType(Paths.get(fileName)))
                .orElse("text/plain");
    }
}