package com.jworks.qup.service.models;

import com.jworks.app.commons.models.PersonInPool;
import com.jworks.app.commons.models.PoolInfo;
import lombok.Builder;
import lombok.Getter;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Builder
@Getter
public class PoolGraduateData {

    private final PersonInPool graduate;
    private final PoolInfo poolInfo;
}
