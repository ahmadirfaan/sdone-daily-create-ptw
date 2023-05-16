package com.sdone.createdailyptw.entity.wizard8;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdone.createdailyptw.entity.BasePtw;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class WizardDelapan extends BasePtw {

    private Otorisasi otorisasi;
}
