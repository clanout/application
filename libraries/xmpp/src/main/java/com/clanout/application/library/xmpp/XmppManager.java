package com.clanout.application.library.xmpp;

import com.clanout.application.framework.conf.ConfLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

public class XmppManager
{
    private static final String TAG = "ChatService";

    private static Logger LOG = LogManager.getRootLogger();

    private static String XMPP_SERVICE_NAME = ConfLoader.getConf(XmppConf.CHAT).get("xmpp.service");
    private static String XMPP_HOST_NAME = ConfLoader.getConf(XmppConf.CHAT).get("xmpp.host");
    private static int XMPP_PORT = Integer.parseInt(ConfLoader.getConf(XmppConf.CHAT).get("xmpp.port"));

    private static String ADMIN_USERNAME = ConfLoader.getConf(XmppConf.CHAT).get("xmpp.admin.username");
    private static String ADMIN_PASSWORD = ConfLoader.getConf(XmppConf.CHAT).get("xmpp.admin.password");
    private static String ADMIN_NICKNAME = ConfLoader.getConf(XmppConf.CHAT).get("xmpp.admin.nickname");

    private static String XMPP_CHATROOM_POSTFIX = ConfLoader.getConf(XmppConf.CHAT).get("xmpp.chatroom_postfix");

    private static XmppManager instance;

    public static void init()
    {
        instance = new XmppManager();
    }

    public static void createUser(String userId)
    {
        synchronized (TAG)
        {
            try
            {
                XMPPConnection connection = instance.getConnection();
                AccountManager accountManager = AccountManager.getInstance(connection);
                accountManager.sensitiveOperationOverInsecureConnection(true);

                accountManager.createAccount(userId, userId);
            }
            catch (Exception e)
            {
                LOG.error("[XMPP] Failed to create user " + userId + " [" + e.getMessage() + "]");
            }
        }
    }

    public static void createChatroom(String mucId)
    {
        synchronized (TAG)
        {
            try
            {
                XMPPConnection connection = instance.getConnection();
                MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
                MultiUserChat muc = mucManager.getMultiUserChat(mucId + XMPP_CHATROOM_POSTFIX);
                muc.create(ADMIN_NICKNAME);
            }
            catch (Exception e)
            {
                LOG.error("[XMPP] Failed to create chatroom " + mucId + " [" + e.getMessage() + "]");
            }
        }
    }

    public static void sendMessage(String mucId, String message)
    {
        synchronized (TAG)
        {
            try
            {
                XMPPConnection connection = instance.getConnection();
                MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
                MultiUserChat muc = mucManager.getMultiUserChat(mucId + XMPP_CHATROOM_POSTFIX);
                muc.join(ADMIN_NICKNAME);
                muc.sendMessage(message);
            }
            catch (Exception e)
            {
                LOG.error("[XMPP] Failed to send message [" + e.getMessage() + "]");
            }
        }
    }

    public static String getAdminNickname()
    {
        return ADMIN_NICKNAME;
    }

    public static void destroy()
    {
        try
        {
            instance.connection.disconnect();
        }
        catch (Exception e)
        {
            LOG.error("[XMPP] Unable close xmpp connection [" + e.getMessage() + "]");
        }
    }

    private AbstractXMPPConnection connection;


    private XmppManager()
    {
        synchronized (TAG)
        {
            try
            {
                XMPPTCPConnectionConfiguration configuration = XMPPTCPConnectionConfiguration
                        .builder()
                        .setUsernameAndPassword(ADMIN_USERNAME, ADMIN_PASSWORD)
                        .setServiceName(XMPP_SERVICE_NAME)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setHost(XMPP_HOST_NAME)
                        .setPort(XMPP_PORT)
                        .build();

                connection = new XMPPTCPConnection(configuration);
                connection.connect();
                connection.login();
            }
            catch (Exception e)
            {
                LOG.error("[XMPP] Unable to open xmpp connection ", e);
            }
        }
    }

    private XMPPConnection getConnection()
    {
        synchronized (TAG)
        {
            try
            {
                if (!connection.isConnected())
                {
                    connection.connect();
                }

                if (!connection.isAuthenticated())
                {
                    connection.login();
                }

                return connection;
            }
            catch (Exception e)
            {
                LOG.error("[XMPP] Unable to get xmpp connection ", e);
                return null;
            }
        }
    }
}
