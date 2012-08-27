package se.liu.tdp024.web.util;

import com.sun.jersey.api.core.PackagesResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class ApplicationConfiguration extends PackagesResourceConfig {

    public ApplicationConfiguration() {
        super("se.liu.tdp024.web.service");
    }
}
