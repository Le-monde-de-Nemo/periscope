package fr.eirb.lemondedenemo.periscope.api.events.manager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Marks a method as an event handler. */
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {}
