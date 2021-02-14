package ch.ojp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

    @RequestMapping("/helloWorld")
    public String call() {
        return "Hello World";
    }
}
