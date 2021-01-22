package api.lemonico.controller;

import api.lemonico.enums.ResponseCode;
import api.lemonico.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class CommonController extends AbstractController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @RequestMapping(method = RequestMethod.GET, path = "/heartbeat")
    public void healthCheck(HttpServletResponse response) throws IOException {

        JsonUtil.writeJson(response, ResponseCode.SUCCESS, null);
    }
}
