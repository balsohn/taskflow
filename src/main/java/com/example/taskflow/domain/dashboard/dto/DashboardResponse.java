package com.example.taskflow.domain.dashboard.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class DashboardResponse {
    private long totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int todoTasks;
    private int overdueTasks;
    private int myTasksToday;
    private double completionRate;

    @QueryProjection

    public DashboardResponse(long totalTasks, int completedTasks, int inProgressTasks, int todoTasks, int overdueTasks, int myTasksToday) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.inProgressTasks = inProgressTasks;
        this.todoTasks = todoTasks;
        this.overdueTasks = overdueTasks;
        this.myTasksToday = myTasksToday;
        if(totalTasks == 0) {
            this.completionRate = 0;
        }
        else {
            this.completionRate = BigDecimal.valueOf((double) completedTasks / totalTasks * 100)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
    }
}
