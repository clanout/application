package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.CollectionUtils;
import com.clanout.application.module.user.domain.observer.UserModuleObservers;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutorService;

@ModuleScope
public class BlockFriends
{
    private ExecutorService backgroundPool;
    private UserRepository userRepository;
    private UserModuleObservers observers;

    @Inject
    public BlockFriends(ExecutorService backgroundPool, UserRepository userRepository, UserModuleObservers observers)
    {
        this.backgroundPool = backgroundPool;
        this.userRepository = userRepository;
        this.observers = observers;
    }

    public void execute(Request request)
    {
        List<String> toBlock = request.toBlock;
        if (!CollectionUtils.isNullOrEmpty(toBlock))
        {
            userRepository.block(request.userId, toBlock);

            backgroundPool.execute(() -> {
                observers.onUsersBlocked(request.userId, toBlock);
            });
        }

        List<String> toUnblock = request.toUnblock;
        if (!CollectionUtils.isNullOrEmpty(toUnblock))
        {
            userRepository.unblock(request.userId, toUnblock);

            backgroundPool.execute(() -> {
                observers.onUsersUnblocked(request.userId, toUnblock);
            });
        }
    }

    public static class Request
    {
        public String userId;
        public List<String> toBlock;
        public List<String> toUnblock;
    }
}
