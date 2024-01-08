package com.outing.outingDashboard.dto;

import com.outing.auth.api.dto.UserDto;

import java.util.List;

public class OutingDto {

    private String id;

    private String description;

    private String outingName;

    private String date;

    private UserDto creatorId;

    private List<OutingDetailsDto> outingDetails;

//    private List<OutingExpensesDto> outingExpenses;

    public OutingDto(String id, String description, String outingName, String date, UserDto creatorId, List<OutingDetailsDto> outingDetails) {
        this.id = id;
        this.description = description;
        this.outingName = outingName;
        this.date = date;
        this.creatorId = creatorId;
        this.outingDetails = outingDetails;
//        this.outingExpenses = outingExpenses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutingName() {
        return outingName;
    }

    public void setOutingName(String outingName) {
        this.outingName = outingName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserDto getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UserDto creatorId) {
        this.creatorId = creatorId;
    }

    public List<OutingDetailsDto> getOutingDetails() {
        return outingDetails;
    }

    public void setOutingDetails(List<OutingDetailsDto> outingDetails) {
        this.outingDetails = outingDetails;
    }

//    public List<OutingExpensesDto> getOutingExpenses() {
//        return outingExpenses;
//    }
//
//    public void setOutingExpenses(List<OutingExpensesDto> outingExpenses) {
//        this.outingExpenses = outingExpenses;
//    }
}
