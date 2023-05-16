package com.sdone.createdailyptw.repository;

import com.sdone.createdailyptw.entity.PtwData;
import com.sdone.createdailyptw.entity.WizardEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PtwDataRepository extends JpaRepository<PtwData, String> {

    List<PtwData> findByUuidAndAndWizard(String uuid, WizardEnum wizardEnum);

}
