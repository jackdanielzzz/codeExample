package com.springbackend.advmanager.repository;

import com.springbackend.advmanager.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "select r from Request r where r.userAgent = :agent " +
                   "and r.ipAddress = :ip and r.date >= :datefrom and r.date <= :dateto")
    List<Request> findByArgs(String agent, String ip, Date datefrom, Date dateto);
}
