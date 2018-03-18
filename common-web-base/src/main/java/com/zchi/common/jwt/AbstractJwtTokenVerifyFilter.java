package com.zchi.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("rawtypes")
public abstract class AbstractJwtTokenVerifyFilter implements Filter {
    protected Logger logger = LogManager.getLogger(getClass());
    static final String FILTER_APPLIED = "__com_htjc_common_JwtTokenVerifyFilter_applied";
    static ObjectMapper objectMapper = new ObjectMapper();
	@Autowired JwtService jwtService;


    @Override public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getAttribute(FILTER_APPLIED) != null) {
            // ensure that filter is only applied once per request
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

        /**
         * 从header获取jwt token
         */
        String jwtToken = request.getHeader("Authorization");
        if (StringUtils.hasLength(jwtToken)) {
            if(jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(6);
                jwtToken = jwtToken.trim();
                try {
                    Jwt jwt = jwtService.decodeAndVerify(jwtToken);
                    /**
                     * 将token解析,从中可以得到登录成功时放入token内的信息
                     */
                    JwtUserClaims jwtData = objectMapper.readValue(jwt.getClaims(), JwtUserClaims.class );
                    Authentication auth = generateAuthentication(jwtData);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    logger.error("decode jwt token failed:" + jwtToken, e);
                    SecurityContextHolder.getContext().setAuthentication(null);
                }
            } else {
                logger.error("token should starts with: Bearer ");
                SecurityContextHolder.getContext().setAuthentication(null);

            }
        } else {
            logger.error("get jwt token failed");
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        chain.doFilter(req, res);
    }

    @Override public void destroy() {
    }
    public abstract Authentication generateAuthentication(JwtUserClaims jwtData) ;
}
