package com.clanout.application.module.auth;

import com.clanout.application.library.util.gson.GsonProvider;
import com.clanout.application.module.auth.domain.service.FacebookService;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        GsonProvider.init();

        String aditya = "CAAKHSfnub5ABANIyxq26ZA6rPh5eT5ZCZBw9YZBH3bluNZBxFAapR6bAcIm8h63ymqh2Rxl8ZBZCh7PZBVhtN2q4VPQRzkBUzz6xkEtEvCN7LP5cR2GbjD03XGh3HxbVL0MFHdJvIgcRskyDvF4aqGWhl0iS1OuEOkkXHf0fEAs6xEDKr2bZCvklszPMftOxHnqAXPzSIL9RZBZAwZDZD";
        String harsh = "EAAKHSfnub5ABANdVrNQnoxozwjtx3vdC4gBsP1g7PDxftWMVBdgVJRT7B465vznrGBd7AqU3uJcmkGQ3ZAlHZC96li0nK8h94CHNTICKZAh4uP0dDxphJtyjYODN9z1TNZAn4mf6APWWgkHjHljG3oQIOcxfeFoZD";

        FacebookService facebookService = new FacebookService(null, null, null);
//        System.out.println(GsonProvider.getGson().toJson(facebookService.getFacebookFriends(harsh)));
    }
}
