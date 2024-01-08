package com.outing.outingDashboard.dto;

import com.outing.auth.api.dto.UserDto;

public class OutingDetailsDto {

    private String id;

    private String outingId;

    private UserDto user;

    private String status;

    public OutingDetailsDto(String id, String outingId, UserDto user) {
        this.id = id;
        this.outingId = outingId;
        this.user = user;
    }
    public OutingDetailsDto(String id, String outingId, UserDto user, String status) {
        this.id = id;
        this.outingId = outingId;
        this.user = user;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOutingId() {
        return outingId;
    }

    public void setOutingId(String outingId) {
        this.outingId = outingId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
