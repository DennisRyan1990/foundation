package com.zchi.common.beans;

/**
 * 分页参数提交
 */
public class PageableRequestBean {
    //当前页码
    private Integer pageIndex = 1;
    //每页记录大小
    private Integer pageSize = 10;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
