package de.ronnywalter.eve.model;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ScheduledTaskId implements Serializable {
    private static final long serialVersionUID = -7592554993165010067L;
    @Column(name = "task_name", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String taskName;

    @Column(name = "task_instance", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String taskInstance;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(String taskInstance) {
        this.taskInstance = taskInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ScheduledTaskId entity = (ScheduledTaskId) o;
        return Objects.equals(this.taskInstance, entity.taskInstance) &&
                Objects.equals(this.taskName, entity.taskName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskInstance, taskName);
    }

}