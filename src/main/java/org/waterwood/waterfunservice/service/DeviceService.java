package org.waterwood.waterfunservice.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.service.Impl.RedisHelper;
import org.waterwood.waterfunservice.utils.security.HashUtil;

import java.util.Base64;
import java.util.Map;

@Service
public class DeviceService{
    private static final String REDIS_KEY_PREFIX = "device";
    private final RedisHelper<String> redisHelper;
    @Getter
    private final String deviceHashSalt = Base64.getEncoder().encodeToString("我这是固定的盐".getBytes());
    private final UserRepository userRepository;

    @Value("${device.temp.ttl:3600}")
    private Long device_temp_ttl;
    @Value("${device.short.ttl:604800}")
    private Long device_short_ttl;
    @Value("${device.long.ttl:777600}")
    private Long device_long_ttl;
    @Value("#{${clean-up.device.max-days} * 24 * 60 * 60 * 1000}")
    private Long deviceExpireMaxTimeMillis;
    protected DeviceService(RedisHelper<String> redisHelper, UserRepository userRepository) {
        this.redisHelper = redisHelper;
        redisHelper.setKeyPrefix(REDIS_KEY_PREFIX);
        this.userRepository = userRepository;
    }

    public String generateAndStoreDeviceId(Long userId, String dfp) {
        String deviceId = this.generateDeviceId(userId,dfp);
        redisHelper.addMapValue(userId.toString(),deviceId, String.valueOf(System.currentTimeMillis()));
        return deviceId;
    }

    public void removeUserDevice(Long userId, String deviceId){
        redisHelper.removeMapFields(userId.toString(),deviceId);
    }

    public void cleanUserDevices(Long userId){
        redisHelper.removeValue(userId.toString());
    }

    public String generateDeviceId(long userId,String dfp){
        return HashUtil.hashWithSalt(dfp+userId, deviceHashSalt);
    }

    @Async
    public void cleanZombieDevicesBatch(int batchSize){
        Page<User> users = userRepository.findAll(PageRequest.of(0, batchSize));
        while(!users.getContent().isEmpty()){
            users.forEach(user->{
                Map<String,Object> devices = redisHelper.getAll(user.getId().toString());
                devices.entrySet().removeIf(
                        entry-> (System.currentTimeMillis() - (long) entry.getValue()) > deviceExpireMaxTimeMillis);
            });
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledCleanup() {
        cleanZombieDevicesBatch(1000);
    }
}