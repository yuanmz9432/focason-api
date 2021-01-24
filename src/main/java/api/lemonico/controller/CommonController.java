package api.lemonico.controller;

import api.lemonico.model.BaseAPIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CommonController extends AbstractController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

    @RequestMapping(method = RequestMethod.GET, path = "/heartbeat")
    public BaseAPIResponse healthCheck() {
        return BaseAPIResponse.success();
    }
}
