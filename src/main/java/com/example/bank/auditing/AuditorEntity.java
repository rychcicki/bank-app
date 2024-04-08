package com.example.bank.auditing;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditorEntity {
    private final String localDateTimePattern = "dd-MMM-YYYY HH:mm:ss";
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = localDateTimePattern)
    private LocalDateTime createdOn;
    @CreatedBy
    private Long createdBy;
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = localDateTimePattern)
    private LocalDateTime updateOn;
    @LastModifiedBy
    private Long updatedBy;
}
