package ru.daru_jo.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.daru_jo.TestAndShutdownController;


@RestController()
@RequestMapping("/v1/system")
public class SystemController extends TestAndShutdownController {
}
