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
public class PtwData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private long id;

    @Enumerated(EnumType.STRING)
    private WizardEnum wizard;

    @Enumerated(EnumType.STRING)
    private WizardStatusEnum status;

    private String uuid;

    @Lob
    private String data;

    private Long timestamp;

}
