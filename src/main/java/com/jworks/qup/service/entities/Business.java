package com.jworks.qup.service.entities;

import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity(name = "businesses")
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "businesses",
        indexes = {
        @Index(name = "STATUS_INDEX", columnList = "status"),
        @Index(name = "BUSINESS_NAME_IDX", columnList = "name")
})
public class Business extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String webSiteUrl;

    @Column(name = "email_address", length = 60, nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "phone_number", length = 20, unique = true, nullable = false)
    private String phoneNumber;

    @Column
    private String storeFrontImageUrl;

    @Column
    private String logoImageUrl;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_category_id", referencedColumnName = "id", nullable = false)
    private BusinessCategory businessCategory;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_user_owner_id", referencedColumnName = "id", nullable = false)
    private EndUser endUser;


}
