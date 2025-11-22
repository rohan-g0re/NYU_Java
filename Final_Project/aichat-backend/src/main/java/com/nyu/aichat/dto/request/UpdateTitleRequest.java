package com.nyu.aichat.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UpdateTitleRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;
    
    public UpdateTitleRequest() {
    }
    
    public UpdateTitleRequest(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}

