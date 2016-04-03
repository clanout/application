package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.common.CollectionUtils;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;
import java.util.List;

@ModuleScope
public class BlockFriends
{
    private UserRepository userRepository;

    @Inject
    public BlockFriends(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public void execute(Request request)
    {
        List<String> toBlock = request.toBlock;
        if (!CollectionUtils.isNullOrEmpty(toBlock))
        {
            userRepository.block(request.userId, toBlock);
        }

        List<String> toUnblock = request.toUnblock;
        if (!CollectionUtils.isNullOrEmpty(toUnblock))
        {
            userRepository.unblock(request.userId, toUnblock);
        }
    }

    public static class Request
    {
        public String userId;
        public List<String> toBlock;
        public List<String> toUnblock;
    }
}
