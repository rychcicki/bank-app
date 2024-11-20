package com.example.bank.auditing;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EqualsAndHashCode(exclude = {"updateOn", "updatedBy"})
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditorEntity {
    @CreatedDate
    private LocalDateTime createdOn;

    @CreatedBy
    private Long createdBy;

    @LastModifiedDate
    private LocalDateTime updateOn;

    @LastModifiedBy
    private Long updatedBy;
}
