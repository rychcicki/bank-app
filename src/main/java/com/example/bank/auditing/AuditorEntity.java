package com.example.bank.auditing;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
    private Integer createdBy;
    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = localDateTimePattern)
    private LocalDateTime updateOn;
    @LastModifiedBy
    private Integer updatedBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = localDateTimePattern)
    /** Jakim cudem będziemy mieć dostęp do tych pól po usunięciu obiektu z bazy?? */
    private LocalDateTime deletedOn;
    private Integer deletedBy;
    private Boolean isDeleted = false;

    @PreUpdate
    @PrePersist
    public void beforeAnyUpdate() {
        if (isDeleted != null && isDeleted) {
            if (deletedBy == null) {
                ApplicationAuditAware applicationAuditAware = new ApplicationAuditAware();
                deletedBy = applicationAuditAware.getCurrentAuditor().orElseThrow();
            }
            if (getDeletedOn() == null) {
                deletedOn = LocalDateTime.now();
            }
        }
    }
}
