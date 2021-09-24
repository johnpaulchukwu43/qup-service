package com.jworks.qup.service.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jworks.app.commons.enums.EntityStatus;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @CreatedDate
    private Timestamp createdAt;

    @LastModifiedDate
    private Timestamp updatedAt;

    @Column(name = "status", nullable = false,length = 20)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("PENDING")
    private EntityStatus entityStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity baseModel = (BaseEntity) o;

        return new EqualsBuilder()
                .append(id, baseModel.id)
                .append(createdAt, baseModel.createdAt)
                .append(updatedAt, baseModel.updatedAt)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(createdAt)
                .append(updatedAt)
                .toHashCode();
    }
}
