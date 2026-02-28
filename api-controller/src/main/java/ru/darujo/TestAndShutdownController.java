package ru.darujo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Configurable
public abstract class TestAndShutdownController {
    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/test")
    public void test() {
    }

    @PostMapping("/shutdownContext")
    @Async
    public void shutdownContext() {
        SpringApplication.exit(applicationContext);
    }
}

