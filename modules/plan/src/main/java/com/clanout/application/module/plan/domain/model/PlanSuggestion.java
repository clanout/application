package com.clanout.application.module.plan.domain.model;

public class PlanSuggestion
{
    private String category;
    private String title;

    public PlanSuggestion(String category, String title)
    {
        this.category = category;
        this.title = title;
    }

    public String getCategory()
    {
        return category;
    }

    public String getTitle()
    {
        return title;
    }
}
