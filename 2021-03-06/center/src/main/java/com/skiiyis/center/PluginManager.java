package com.skiiyis.center;

import java.util.HashMap;

public class PluginManager {

    private static final HashMap<Class, Object> cache = new HashMap<>();

    static public <T> T getPlugin(Class<T> clazz) {
        Object c = cache.get(clazz);
        if (c == null) return null;
        return ((T) c);
    }

    static {
        // cache.put(Plugin.class, new PluginManager());
        // cache.put(IUserPlugin.class,new UserPlugin())
        // cache.put(IShopPlugin.class,new ShopPlugin())
    }
}
