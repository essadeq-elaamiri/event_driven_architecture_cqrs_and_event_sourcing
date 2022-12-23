package me.elaamiri.accountcqrseventsourcing.common_api.dtos.query;

import me.elaamiri.accountcqrseventsourcing.common_api.enumerations.OperationType;

import java.util.Date;

public class OperationResponseDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType operationType;
}
