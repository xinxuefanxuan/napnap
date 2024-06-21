package com.work37.napnap.Game;

public class PostRequest {
    private int current;
    private int pageSize;
    private Long postId;
    private String sortField;

    public PostRequest() {
    }

    public PostRequest(int current, int pageSize, Long postId, String sortField) {
        this.current = current;
        this.pageSize = pageSize;
        this.postId = postId;
        this.sortField = sortField;
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

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
}
