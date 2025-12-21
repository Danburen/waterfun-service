package org.waterwood.waterfunservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.service.account.AccountService;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final AccountService accountService;

    @Async
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanUnverifiedEmail() {
        accountService.cleanUnverifiedEmail();
    }
}
