package com.lemonico.common.controller;



import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BasicController
{
    protected final HttpServletResponse response;
}
