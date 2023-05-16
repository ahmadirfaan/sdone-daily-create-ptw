package com.sdone.createdailyptw.repository;

import com.sdone.createdailyptw.entity.PtwData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtwDataRepository extends JpaRepository<PtwData, String> {
}
