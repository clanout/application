package com.clanout.application.library.xmpp;

import com.clanout.application.framework.lib.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XmppLibrary implements Library
{
    private static Logger LOG = LogManager.getRootLogger();

    public XmppLibrary() throws Exception
    {
        LOG.debug("[XmppLibrary initialized]");
        XmppManager.init();
    }

    @Override
    public void destroy()
    {
        XmppManager.destroy();
        LOG.debug("[XmppLibrary destroyed]");
    }
}
