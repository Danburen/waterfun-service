package org.waterwood.waterfunservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.waterwood.waterfunservice.dto.response.comm.ApiResponse;
import org.waterwood.waterfunservice.infrastructure.utils.io.oss.CloudFileUploadService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class FileUploadController {
    private final CloudFileUploadService cloudFileUploadService;

    public FileUploadController(CloudFileUploadService cloudFileUploadService) {
        this.cloudFileUploadService = cloudFileUploadService;
    }

    @PostMapping("/upload")
    public ApiResponse<String> upload(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf('.'));
        String key = "imgs/" + UUID.randomUUID().toString() + ext;
        log.info("upload file: {}", key);
        cloudFileUploadService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
        return ApiResponse.success();
    }
}
