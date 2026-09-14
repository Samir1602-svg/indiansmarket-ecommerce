package com.example.ecommerce;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OpsController {

    @GetMapping({"/ops", "/admin", "/internal/ops"})
    public String redirectToOps() {
        return "forward:/internal/ops.html";
    }
}