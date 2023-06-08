package com.work.filter;

import com.alibaba.fastjson.JSON;
import com.sun.deploy.net.HttpResponse;
import com.work.common.BaseContext;
import com.work.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否完成登录，过滤器的名字loginCheckFilter，urlPatterns拦截哪些路径：所有请求都拦截
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符，进行路径比较
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取请求的URI
        String requestURI = request.getRequestURI();

        //{}占位符，显示request.getRequestURI()的内容
        log.info("拦截到请求：{}",request.getRequestURI());
        //放行不拦截的路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"   //移动端登录
        };
        //判断此次请求是否需要处理
        boolean check=check(urls,requestURI);
        //不处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //员工：要处理，已登录，放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，id为{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            //已登录，将用户id存入线程
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //用户：要处理，已登录，放行
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，id为{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            //已登录，将用户id存入线程
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("未登录");
        //未登录，通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    //路径匹配，本次请求是否放行
    public boolean check(String[] urls,String requestURI){
        for (String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
