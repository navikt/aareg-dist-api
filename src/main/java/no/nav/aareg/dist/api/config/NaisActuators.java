package no.nav.aareg.dist.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal")
@SuppressWarnings("EmptyMethod")
@RequiredArgsConstructor
public class NaisActuators {

    private final ThreadPoolTaskExecutor executor;

    @GetMapping(path = "isReady")
    void isReady() {
        //do nothing
    }

    @GetMapping(path = "isAlive")
    void isAlive() {
        //do nothing
    }

    @GetMapping(path = "shutdown")
    void shutdown() throws InterruptedException {
        log.info("preStopHook:START");

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(5);
        executor.shutdown();
        Thread.sleep(5000); // Venter litt ekstra i et forsøk på å sikre en "graceful shutdown"
        log.info("preStopHook:FINISH");
    }
}
