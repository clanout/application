package com.clanout.application.module.plan.domain.repository;

import com.clanout.application.module.plan.domain.exception.PlanNotFoundException;
import com.clanout.application.module.plan.domain.model.Attendee;
import com.clanout.application.module.plan.domain.model.Location;
import com.clanout.application.module.plan.domain.model.Plan;
import com.clanout.application.module.plan.domain.model.PlanSuggestion;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PlanRepository
{
    Plan fetch(String planId) throws PlanNotFoundException;

    Plan create(Plan plan);

    void delete(String planId);

    void update(String planId, String description, OffsetDateTime startTime,
                OffsetDateTime endTime, Location location);

    String getAttendeeName(String userId);

    void addAttendee(String planId, Attendee attendee);

    void deleteAttendee(String planId, String userId);

    void updateStatus(String planId, String userId, String status);

    void addPhoneInvitations(String planId, String userId, List<String> mobileNumbers);

    Map<String, Set<String>> fetchPendingInvitations(String mobileNumber);

    void deletePendingInvitations(String mobileNumber);

    List<PlanSuggestion> fetchCreateSuggestions();

    List<Plan> fetchExpiredPlans(OffsetDateTime timestamp);

    void archive(Plan plan);
}
