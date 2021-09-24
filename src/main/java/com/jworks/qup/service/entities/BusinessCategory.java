package com.jworks.qup.service.entities;

import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "business_categories", indexes = {
            @Index(name = "BUSINESS_CATEGORY_NAME_IDX", columnList = "name"),
            @Index(name = "STATUS_INDEX", columnList = "status")
})
public class BusinessCategory extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column
    private String description;
}
