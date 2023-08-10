package com.imooc.employee.dao;

import com.imooc.employee.entity.EmployeeActivityEntity;
import com.imooc.employee.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeActivityDao extends JpaRepository<EmployeeActivityEntity, Long> {

    int countByEmployeeIdAndActivityTypeAndActive(Long employeeId,
                                                 ActivityType activityType,
                                                 boolean active);
    Optional<EmployeeActivityEntity> findByEmployeeId(Long employeeId);



}
