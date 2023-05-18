package com.sdone.createdailyptw.repository;

import com.sdone.createdailyptw.entity.PtwData;
import com.sdone.createdailyptw.entity.WizardEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PtwDataRepository extends JpaRepository<PtwData, String> {

    List<PtwData> findByUuidAndAndWizardAndLocalDate(String uuid, WizardEnum wizardEnum, LocalDate localDate);

}
