package com.sdone.createdailyptw.entity.wizard7;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdone.createdailyptw.entity.BasePtw;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class WizardTujuh extends BasePtw {

    private WorkingSafety safety;
}
