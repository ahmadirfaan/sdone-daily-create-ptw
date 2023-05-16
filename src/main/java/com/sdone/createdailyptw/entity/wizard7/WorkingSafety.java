package com.sdone.createdailyptw.entity.wizard7;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class WorkingSafety {

    private String sortCircuit;

    private String markerBoards;

    private String equipmentIsolation;

    private String keselamatanLainnya;

    private String pengaturanKomunikasi;

    private String titikKeluarDarurat;
}
