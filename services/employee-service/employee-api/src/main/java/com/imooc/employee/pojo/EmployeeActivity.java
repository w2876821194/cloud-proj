package com.imooc.employee.pojo;

import com.imooc.employee.enums.ActivityType;
import lombok.Data;

import java.util.Date;

@Data
public class EmployeeActivity {

    private Long id;

    private Long employeeId;

    private ActivityType activityType;

    private Long resourceId;

    private Date startTime;

    private Date endTime;

    private boolean active = true;
}
