package com.work37.napnap.ui.search;

import java.util.List;

public class SearchRequest {
    private int current;
    private int pageSize;
    private String searchText;
    private String sortField;
    List<String> tagList;

    public SearchRequest() {
    }

    public SearchRequest(int current, int pageSize, String searchText, String sortField, List<String> tagList) {
        this.current = current;
        this.pageSize = pageSize;
        this.searchText = searchText;
        this.sortField = sortField;
        this.tagList = tagList;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
                "current=" + current +
                ", pageSize=" + pageSize +
                ", searchText='" + searchText + '\'' +
                ", sortField='" + sortField + '\'' +
                ", tagList=" + tagList +
                '}';
    }
}
