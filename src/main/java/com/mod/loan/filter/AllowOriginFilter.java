//package com.mod.loan.filter;
//
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class AllowOriginFilter implements Filter {
//
//    @Override
//    public void destroy() {
//    }
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
//        HttpServletResponse response = (HttpServletResponse) res;
//        response.setHeader("Access-Control-Allow-Origin", "*");//* 表示该资源谁都可以用,从而实现跨域
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type");
//        HttpServletRequest request = (HttpServletRequest) req;
//
//        try {
//            chain.doFilter(req, res);
//        } catch (IOException | ServletException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void init(FilterConfig arg0) {
//    }
//
//}