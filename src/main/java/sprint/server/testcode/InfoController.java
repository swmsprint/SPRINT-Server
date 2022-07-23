package sprint.server.testcode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sprint.server.testcode.Info;
import sprint.server.testcode.InfoService;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RequiredArgsConstructor
@Controller
public class InfoController {
    /**
     *
     * welcome반환
//     */
    private final InfoService infoService;

    @GetMapping("/api/hello")
    @ResponseBody
    public String apiWelcome(){
        return "hello world!";

    }

    @GetMapping("/test")
    public String getText(HttpServletRequest request){
//        log.info("get parameter" + request.getParameter("text"));
        System.out.println("get parameter" + request.getParameter("text"));
        Info info = new Info();
        info.setContent(request.getParameter("text"));

        infoService.join(info);

        return "/test/result";
    }



}
