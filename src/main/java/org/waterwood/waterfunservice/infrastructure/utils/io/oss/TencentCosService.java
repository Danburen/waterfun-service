package org.waterwood.waterfunservice.infrastructure.utils.io.oss;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class TencentCosService implements CloudFileUploadService{
    private final COSClient cosClient;
    @Value("${tencent.cos.bucket-name}")
    private String bucketName;
    public TencentCosService(COSClient cosClient){
        this.cosClient = cosClient;
    }

    @Override
    public void uploadFile(String key, InputStream stream, long size, String contentType) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(size);
        meta.setContentType(contentType);
        PutObjectRequest putRequ = new PutObjectRequest(bucketName, key,stream, meta);
        cosClient.putObject(putRequ);
    }
}
