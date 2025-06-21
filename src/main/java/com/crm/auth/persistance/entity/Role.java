package com.crm.auth.persistance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.collection.spi.PersistentList;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private Boolean isDeletable;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "role",
            orphanRemoval = true
    )
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<AccessControl> accessControls = new ArrayList<>();

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updateAt;

    public List<AccessControl> getAccessControls() {
        return Collections.unmodifiableList(accessControls);
    }

    public void addAccessControls(List<AccessControl> accessControls) {
        if (accessControls instanceof PersistentList) {
            this.accessControls = accessControls;
        } else {
            this.accessControls.clear();
            this.accessControls.addAll(accessControls);
        }
    }

    public void addAccessControl(AccessControl accessControl) {
        this.accessControls.add(accessControl);
    }

}
