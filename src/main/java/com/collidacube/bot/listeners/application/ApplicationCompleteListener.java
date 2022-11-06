package com.collidacube.bot.listeners.application;

import java.util.function.BiConsumer;

import com.collidacube.javacordext.applications.Application;
import com.collidacube.javacordext.listeners.Listener;

public interface ApplicationCompleteListener extends BiConsumer<Application, Throwable>, Listener  {
    
}
