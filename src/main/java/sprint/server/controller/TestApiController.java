package sprint.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {

    @GetMapping("/user/test")
    public String test1() {
        return "test";
    }

    @GetMapping("/admin/test")
    public String test2() {
        return "test2";
    }
}
