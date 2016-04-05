package com.clanout.application.module.plan.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

public class Feed
{
    private OffsetDateTime updatedAt;
    private List<Plan> plans;

    public OffsetDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public List<Plan> getPlans()
    {
        return plans;
    }

    public void setPlans(List<Plan> plans)
    {
        this.plans = plans;
    }
}
