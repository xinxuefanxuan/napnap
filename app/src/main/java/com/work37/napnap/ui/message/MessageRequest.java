package com.work37.napnap.ui.message;

public class MessageRequest {
    private int isVisible;
    private int current;
    private int pageSize;
    private String sortField;

    public MessageRequest(int isVisible, int current, int pageSize) {
        this.isVisible = isVisible;
        this.current = current;
        this.pageSize = pageSize;
        this.sortField = "";
    }

    public int getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(int isVisible) {
        this.isVisible = isVisible;
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
}
