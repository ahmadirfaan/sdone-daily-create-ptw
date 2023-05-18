package com.sdone.createdailyptw.entity.wizard6;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties
public class Implementasi {

    private String tanggalPtw;

    private String titikAksesLokasi;

    private String jumlahPekerja;

    private List<String> fotoPekerjaan;
}
