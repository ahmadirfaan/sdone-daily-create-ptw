package com.sdone.createdailyptw.entity.wizard6;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdone.createdailyptw.entity.BasePtw;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class WizardEnam extends BasePtw {

   private Implementasi implementasi;
}
