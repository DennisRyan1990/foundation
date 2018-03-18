package com.zchi.common.jwt;

import com.zchi.common.utils.Assert;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken implements Serializable{
    private static final long serialVersionUID = 42L;
    /**
     * 用户唯一标识
     */
    private Integer userId;
    /**
     * 所属系统名称:${spring.application.name}
     */
    private String applicationName;
    private Object principal;
    private Object credentials;
    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public JwtAuthentication(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public JwtAuthentication(Integer userId,String applicationName,Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.notNull(userId);
        Assert.notBlank(applicationName);
        this.userId = userId;
    }
    @Override public Object getCredentials() {
        return this.credentials;
    }

    @Override public Object getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
