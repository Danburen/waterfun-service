package org.waterwood.waterfunservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservicecore.api.PostPolicyDto;
import org.waterwood.waterfunservicecore.services.storage.CloudFileService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class FileUploadController {
    private final CloudFileService cloudFileService;

    public FileUploadController(CloudFileService cloudFileService) {
        this.cloudFileService = cloudFileService;
    }

    @PostMapping("/upload")
    public ApiResponse<String> upload(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf('.'));
        String key = "imgs/" + UUID.randomUUID().toString() + ext;
        log.info("upload file: {}", key);
        cloudFileService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
        return ApiResponse.success();
    }

    @GetMapping("/upload/policy")
    public ApiResponse<PostPolicyDto> policy(@RequestParam String suffix) {
        return ApiResponse.success(cloudFileService.buildPutPolicy("common", suffix));
    }
}
