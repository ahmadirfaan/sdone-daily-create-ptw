package com.sdone.createdailyptw.entity;

import lombok.Data;

@Data
public class BasePtw {

    private long timestamp;

    private String uuid;

    private String username;

    private WizardStatusEnum wizardStatusEnum;
}
