package com.clanout.application.library.util.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneValidator
{
    private static final String DEFAULT_DOUNTRY_CODE = "IN";

    public static boolean isValid(String phone)
    {
        try
        {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parseAndKeepRawInput(phone, DEFAULT_DOUNTRY_CODE);
            if (phoneNumberUtil.isValidNumber(phoneNumber) && phoneNumber.getCountryCodeSource() != Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (NumberParseException e)
        {
            return false;
        }
    }
}
