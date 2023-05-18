package com.sdone.createdailyptw.entity.wizard8;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties
public class Otorisasi {

    private String occLineOperator;

    private String occUsername;

    private List<String> stasiunOperator;

    private String aksesDiberikan;

    private String alasanTolak;
}
