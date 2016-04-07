package com.clanout.application.module.plan.domain.observer;

public interface ChatUpdateObserver
{
    void onChatUpdate(String planId, String message);
}
