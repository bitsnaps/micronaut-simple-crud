package simple.crud;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/")
public class HomController {

    @Get("/")
    public String home(){
        return "Welcome!";
    }

}
