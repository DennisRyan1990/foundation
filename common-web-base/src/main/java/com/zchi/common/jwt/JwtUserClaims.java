package com.zchi.common.jwt;

import com.zchi.common.utils.Assert;

import java.io.Serializable;

/**
 * Created by zchi on 16/7/4.
 */
public class JwtUserClaims implements Serializable {
	private static final long serialVersionUID = 1L;

	public JwtUserClaims() {}
    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户名,在系统内唯一标识用户
     */
    private String name;

    /**
     * 用户手机号,唯一标识用户
     */
    private String mob;

    /**
     * 用户超时 时间戳
     */
    private Long exp;

    /**
     * 用户所属系统
     */
    private String sys;

    /**
     * 用户登录序号
     */
    private Integer seq;

    public JwtUserClaims(Integer id,String sys) {
        Assert.notNull(id);
        Assert.notBlank(sys);
        this.id = id;
        this.sys = sys;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
