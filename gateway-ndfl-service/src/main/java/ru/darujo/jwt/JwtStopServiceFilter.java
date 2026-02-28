package ru.darujo.jwt;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JwtStopServiceFilter extends JwtRightFilter {
    @Override
    protected List<String> getRight() {

        List<String> rights = new ArrayList<>();
        rights.add("STOP_SERVICE");
        return rights;
    }
}
