package com.work37.napnap.Game;

import java.util.List;

public class GameRequest {
    private int current;
    private int pageSize;
    private String searchText;
    private String sortField;
    private List<String> tagList;

    public GameRequest() {
    }

    public GameRequest(int current, int pageSize, String searchText, String sortField, List<String> tagList) {
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
}
