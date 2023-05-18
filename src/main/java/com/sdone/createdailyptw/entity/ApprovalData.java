package com.sdone.createdailyptw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ApprovalData {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private long id;

    private String catatan;

    private String namaPetugas;

    private String unitPetugas;

    private String status;

    private String username;

    private String role;

    private String uuid;

    @Column()
    private Long timestamp;

}
