package org.obridge.coconut.scheduler;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BackgroundProcessing implements Scheduler {

    private final ScheduledExecutorService ses;

    public BackgroundProcessing() {
        this.ses = Executors.newScheduledThreadPool(3);
    }

    @Override
    public Scheduler register(Runnable task, int delayMinutes) {

        log.info(task.getClass().getName() + " added to scheduler - run in every " + delayMinutes + " minutes");

        this.ses.scheduleWithFixedDelay(() -> {
            log.info("Starting : " + task.getClass().getName());
            task.run();
            log.info("Finished : " + task.getClass().getName());
        }, 0, delayMinutes, TimeUnit.MINUTES);

        return this;
    }


}
