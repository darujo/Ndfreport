package ru.darujo.jwt;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAdminFilter extends JwtRightFilter {
    @Override
    protected List<String> getRight() {
        List<String> rights = new ArrayList<>();
        rights.add("EDIT_USER");
        rights.add("ADMIN_USER");
        return rights;
    }
}
