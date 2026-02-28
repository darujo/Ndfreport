package ru.daru_jo.model;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class RunnableNotException implements Runnable {
    private final Runnable runnable;

    public RunnableNotException(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception ex) {
            log.error(Boolean.toString(Thread.interrupted()));
            if (Thread.interrupted()) {
                throw new RuntimeException("Команда на завершение");
            }
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
        }
    }
}
