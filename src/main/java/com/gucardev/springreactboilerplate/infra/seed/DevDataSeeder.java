package com.gucardev.springreactboilerplate.infra.seed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements ApplicationRunner {

    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(ApplicationArguments args) {
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            // enable it later when you have something to seed
//            if (repository.existsByName("DEMO_NAME")) {
//                log.info("Dev seed skipped! Already exists.");
//                return;
//            }
            log.info("Dev seed: creating example data.");

            log.info("Dev seed: done.");
        });
    }

}
