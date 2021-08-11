package com.jworks.qup.service.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity(name = "end_user_pool_config")
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "end_user_pool_config", indexes = @Index(name = "STATUS_INDEX", columnList = "status"))
public class EndUserPoolConfig extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "capacity", nullable = false)
    private Long capacity;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_id", referencedColumnName = "id", nullable = false)
    private EndUserQueue endUserQueue;
}

