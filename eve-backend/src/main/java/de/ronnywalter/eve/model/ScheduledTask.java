package de.ronnywalter.eve.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "scheduled_tasks", indexes = {
        @Index(name = "last_heartbeat_idx", columnList = "last_heartbeat"),
        @Index(name = "execution_time_idx", columnList = "execution_time")
})
public class ScheduledTask {
    @EmbeddedId
    private ScheduledTaskId id;

    @Column(name = "task_data")
    private byte[] taskData;

    @Column(name = "execution_time", nullable = false)
    private OffsetDateTime executionTime;

    @Column(name = "picked", nullable = false)
    private Boolean picked = false;

    @Column(name = "picked_by")
    @Type(type = "org.hibernate.type.TextType")
    private String pickedBy;

    @Column(name = "last_success")
    private OffsetDateTime lastSuccess;

    @Column(name = "last_failure")
    private OffsetDateTime lastFailure;

    @Column(name = "consecutive_failures")
    private Integer consecutiveFailures;

    @Column(name = "last_heartbeat")
    private OffsetDateTime lastHeartbeat;

    @Column(name = "version", nullable = false)
    private Long version;

    public ScheduledTaskId getId() {
        return id;
    }

    public void setId(ScheduledTaskId id) {
        this.id = id;
    }

    public byte[] getTaskData() {
        return taskData;
    }

    public void setTaskData(byte[] taskData) {
        this.taskData = taskData;
    }

    public OffsetDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(OffsetDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public Boolean getPicked() {
        return picked;
    }

    public void setPicked(Boolean picked) {
        this.picked = picked;
    }

    public String getPickedBy() {
        return pickedBy;
    }

    public void setPickedBy(String pickedBy) {
        this.pickedBy = pickedBy;
    }

    public OffsetDateTime getLastSuccess() {
        return lastSuccess;
    }

    public void setLastSuccess(OffsetDateTime lastSuccess) {
        this.lastSuccess = lastSuccess;
    }

    public OffsetDateTime getLastFailure() {
        return lastFailure;
    }

    public void setLastFailure(OffsetDateTime lastFailure) {
        this.lastFailure = lastFailure;
    }

    public Integer getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public void setConsecutiveFailures(Integer consecutiveFailures) {
        this.consecutiveFailures = consecutiveFailures;
    }

    public OffsetDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(OffsetDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}