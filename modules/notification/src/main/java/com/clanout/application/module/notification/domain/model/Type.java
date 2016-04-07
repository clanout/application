package com.clanout.application.module.notification.domain.model;

public enum Type
{
    /* New Plan Created */
    PLAN_CREATED,

    /* Plan Deleted by creator */
    PLAN_DELETED,

    /* Plan Details Updated */
    PLAN_UPDATED,

    /* Friends Joined Plan */
    PLAN_FRIENDS_JOINED,

    /* Plan Removed (from feed) */
    PLAN_REMOVE_FROM_FEED,

    /* Invitation */
    PLAN_INVITATION,

    /* Chat Update */
    PLAN_CHAT,

    /* Status Update */
    PLAN_STATUS,

    /* Friend Relocated */
    FRIEND_RELOCATED,

    /* New Friends joined app */
    FRIEND_NEW,

    /* Blocked by friend */
    FRIEND_BLOCKED,

    /* Unblocked by friend */
    FRIEND_UNBLOCKED
}
