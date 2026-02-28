package ru.darujo.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.darujo.TestAndShutdownController;


@RestController()
@RequestMapping("/v1/system")
public class SystemController extends TestAndShutdownController {
}
