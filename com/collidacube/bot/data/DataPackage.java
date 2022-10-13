package com.collidacube.bot.data;

import java.util.HashMap;

public abstract class DataPackage<T extends DataPackage<?>> {

    protected DataPackage(Class<T> subClass, DataManager<T> manager) {
        if (manager == null && !DataManager.isFinishedLoading()) return;

        if (subClass.isInstance(this)) manager.register((T)this);
        else throw new RuntimeException("Invalid data manager!");
    }

    public abstract HashMap<String, String> getData();

}
