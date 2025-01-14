package com.jsh.erp.filter;

import com.jsh.erp.service.redis.RedisService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LogCostFilter", urlPatterns = {"/*"},
        initParams = {@WebInitParam(name = "filterPath",
                      value = "/jshERP-boot/user/login#/jshERP-boot/user/weixinLogin#/jshERP-boot/user/weixinBind#" +
                              "/jshERP-boot/user/registerUser#/jshERP-boot/user/randomImage#" +
                              "/jshERP-boot/inOutItem/findBySelect#" +
                              "/jshERP-boot/platformConfig/getPlatform#/jshERP-boot/v2/api-docs#/jshERP-boot/webjars#" +
                              "/jshERP-boot/systemConfig/static#/jshERP-boot/api/plugin/wechat/weChat/share#" +
                              "/jshERP-boot/api/plugin/general-ledger/pdf/voucher#/jshERP-boot/api/plugin/tenant-statistics/tenantClean")})
public class LogCostFilter implements Filter {

    private static final String FILTER_PATH = "filterPath";

    private String[] allowUrls;
    @Resource
    private RedisService redisService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String filterPath = filterConfig.getInitParameter(FILTER_PATH);
        if (!StringUtils.isEmpty(filterPath)) {
            allowUrls = filterPath.contains("#") ? filterPath.split("#") : new String[]{filterPath};
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String requestUrl = servletRequest.getRequestURI();
        //具体，比如：处理若用户未登录，则跳转到登录页
        Object userId = redisService.getObjectFromSessionByKey(servletRequest,"userId");
        if(userId!=null) { //如果已登录，不阻止
            chain.doFilter(request, response);
            return;
        }
        if (requestUrl != null && (requestUrl.contains("/doc.html") ||
            requestUrl.contains("/user/login") || requestUrl.contains("/user/register"))) {
            chain.doFilter(request, response);
            return;
        }
        if (null != allowUrls && allowUrls.length > 0) {
            for (String url : allowUrls) {
                if (requestUrl.startsWith(url)) {
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        servletResponse.setStatus(500);
        if(requestUrl != null && !requestUrl.contains("/user/logout") && !requestUrl.contains("/function/findMenuByPNumber")) {
            servletResponse.getWriter().write("loginOut");
        }
    }

    @Override
    public void destroy() {

    }
}