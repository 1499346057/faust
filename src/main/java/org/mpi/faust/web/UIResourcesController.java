package org.mpi.faust.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@CrossOrigin
public class UIResourcesController {
    @GetMapping(value = "/**/{path:[^\\.]*}")
    public ModelAndView forward() {
        return new ModelAndView("/index.html");
    }
}
