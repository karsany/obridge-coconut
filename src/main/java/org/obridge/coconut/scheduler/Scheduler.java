package org.obridge.coconut.scheduler;

public interface Scheduler {

    Scheduler register(Runnable task, int delayMinutes);

}
