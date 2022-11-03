package sprint.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestApiController {

    private final Environment env;
    @GetMapping("/")
    public String getEnv() {
        List<String> profile = Arrays.asList(env.getActiveProfiles());
        List<String> realProfiles = Arrays.asList("dev","dev_release_login", "release", "release_login8081", "release_login8082");
        String defaultProfile = profile.isEmpty() ? "default" : profile.get(0);

        return profile.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
    @GetMapping("/user/test")
    public String test1() {
        return "test";
    }
    @GetMapping("/api/test")
    public String test3() {return "test3";}
    @GetMapping("/admin/test")
    public String test2() {
        return "test2";
    }
}
