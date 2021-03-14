package com.skiiyis.user;

import com.skiiyis.center.PluginImpl;
import com.skiiyis.i_user.IUserPlugin;
import com.skiiyis.i_user.User;

@PluginImpl(IUserPlugin.class)
public class UserPlugin implements IUserPlugin {

    @Override
    public User getUser() {
        return new User("这是一个user");
    }
}
