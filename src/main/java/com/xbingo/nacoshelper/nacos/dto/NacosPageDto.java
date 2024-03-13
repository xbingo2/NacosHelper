package com.xbingo.nacoshelper.nacos.dto;

import java.util.List;

public class NacosPageDto {
    private Integer pageNumber;

    private List<NacosConfigDto> pageItems;

    private Integer totalCount;

    private Integer pagesAvailable;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<NacosConfigDto> getPageItems() {
        return pageItems;
    }

    public void setPageItems(List<NacosConfigDto> pageItems) {
        this.pageItems = pageItems;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPagesAvailable() {
        return pagesAvailable;
    }

    public void setPagesAvailable(Integer pagesAvailable) {
        this.pagesAvailable = pagesAvailable;
    }
}
