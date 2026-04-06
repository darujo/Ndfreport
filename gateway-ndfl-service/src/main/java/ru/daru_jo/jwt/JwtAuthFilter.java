package ru.daru_jo.jwt;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthFilter extends JwtRightFilter {

    @Override
    protected List<String> getRight() {
        return null;
    }
}
