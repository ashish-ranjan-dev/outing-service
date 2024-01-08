package com.outing.outingDashboard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outingDetails")
public class OutingDetailsModel {

    @Id
    private String id;

    @Column(name = "outing_id")
    private String outingId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private String status;

    public OutingDetailsModel(){}
    public OutingDetailsModel(String id, String outingId, String userId) {
        this.id = id;
        this.outingId = outingId;
        this.userId = userId;
    }

    public OutingDetailsModel(String id, String outingId, String userId, String status) {
        this.id = id;
        this.outingId = outingId;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
