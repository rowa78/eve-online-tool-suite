package de.ronnywalter.eve.jobs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SchedulableJob {
    public String initScheduleTime() default "";
    public String workerScheduleTime() default "";
    public String secondsDelay() default "0";
}
