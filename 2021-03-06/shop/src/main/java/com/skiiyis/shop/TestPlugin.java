package com.skiiyis.shop;

import com.skiiyis.center.PluginManager;
import com.skiiyis.i_user.IUserPlugin;
import com.skiiyis.i_user.User;

public class TestPlugin {
    public User test() {
        IUserPlugin plugin = PluginManager.getPlugin(IUserPlugin.class);
        return plugin == null ? null : plugin.getUser();
    }
}
