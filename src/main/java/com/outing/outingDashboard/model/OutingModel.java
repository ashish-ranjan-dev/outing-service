package com.outing.outingDashboard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outing")
public class OutingModel {

    @Id
    private String id;

    @Column(name = "date")
    private String date;

    @Column(name = "outing_name")
    private String outingName;

    @Column(name = "description")
    private String description;

    @Column(name = "deleted_status")
    private boolean deletedStatus;

    @Column(name = "creator")
    private String creatorId;

    public OutingModel(){}
    public OutingModel(String id, String date, String outingName, String description, boolean deletedStatus, String creatorId) {
        this.id = id;
        this.date = date;
        this.outingName = outingName;
        this.description = description;
        this.deletedStatus = deletedStatus;
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOutingName() {
        return outingName;
    }

    public void setOutingName(String outingName) {
        this.outingName = outingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeletedStatus() {
        return deletedStatus;
    }

    public void setDeletedStatus(boolean deletedStatus) {
        this.deletedStatus = deletedStatus;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
