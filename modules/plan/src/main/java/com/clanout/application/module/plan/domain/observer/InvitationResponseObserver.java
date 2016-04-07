package com.clanout.application.module.plan.domain.observer;

public interface InvitationResponseObserver
{
    void onInvitationResponse(String planId, String userId, String invitationResponse);
}
