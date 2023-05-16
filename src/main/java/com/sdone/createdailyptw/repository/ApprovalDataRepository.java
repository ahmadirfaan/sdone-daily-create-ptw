package com.sdone.createdailyptw.repository;

import com.sdone.createdailyptw.entity.ApprovalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalDataRepository extends JpaRepository<ApprovalData, String> {

    List<ApprovalData> findByUuid(String uuid);
}
