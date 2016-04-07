package com.clanout.application.library.xmpp;


import com.clanout.application.framework.conf.ConfResource;

public class XmppConf extends ConfResource
{
    public static final XmppConf CHAT = new XmppConf("xmpp");

    private XmppConf(String filename)
    {
        super(filename);
    }
}
