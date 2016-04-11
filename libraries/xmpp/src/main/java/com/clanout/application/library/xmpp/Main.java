package com.clanout.application.library.xmpp;

public class Main
{
    public static void main(String[] args)
    {
        XmppManager.init();

        XmppManager.createUser("test_user");
//        XmppManager.sendMessage("dummy", "Hello again!!");

        XmppManager.destroy();
    }
}
