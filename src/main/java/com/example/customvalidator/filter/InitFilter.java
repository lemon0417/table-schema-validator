package com.example.customvalidator.filter;

import com.example.customvalidator.result.ResultHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class InitFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(
            ServletRequest req
            , ServletResponse res
            , FilterChain chain
    ) throws IOException, ServletException {
        ResultHolder.init();
        chain.doFilter(req, res);
    }
}
