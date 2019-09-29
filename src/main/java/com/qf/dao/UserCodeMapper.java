package com.qf.dao;

import com.qf.domain.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserCodeMapper extends JpaRepository<UserCode,Integer> {
    UserCode findByQqEmail(String qqEmail);
}
