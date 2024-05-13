package carrotbat410.lol.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    //TODO @CreationTimestamp vs @PrePersist
//    @CreationTimestamp
    private LocalDateTime created_at;
//    @UpdateTimestamp
    private LocalDateTime updated_at;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        created_at = now;
        updated_at = now;
    }

    public BaseEntity() {
        this.updated_at = LocalDateTime.now();
    }

}
