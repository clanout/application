package com.clanout.application.module.user.domain.use_case;

import com.clanout.application.framework.di.ModuleScope;
import com.clanout.application.library.util.validation.PhoneValidator;
import com.clanout.application.module.user.domain.exception.InvalidUserFieldException;
import com.clanout.application.module.user.domain.repository.UserRepository;

import javax.inject.Inject;

@ModuleScope
public class UpdateMobile
{
    private UserRepository userRepository;

    @Inject
    public UpdateMobile(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public void execute(Request request) throws InvalidUserFieldException
    {
        if (!PhoneValidator.isValid(request.mobileNumber))
        {
            throw new InvalidUserFieldException("mobile number");
        }

        userRepository.updateMobileNumber(request.userId, request.mobileNumber);
    }

    public static class Request
    {
        public String userId;
        public String mobileNumber;
    }
}
