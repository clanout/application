package com.clanout.application.framework.conf;

import java.util.List;

public interface Conf
{
    String get(String key);

    List<String> getList(String key);
}
