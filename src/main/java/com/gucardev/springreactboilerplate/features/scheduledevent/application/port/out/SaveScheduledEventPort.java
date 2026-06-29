package com.gucardev.springreactboilerplate.features.scheduledevent.application.port.out;

import com.gucardev.springreactboilerplate.features.scheduledevent.domain.model.ScheduledEvent;

/**
 * Output port: persist a scheduled event (insert or update) and return the stored state, including
 * any generated id and audit metadata.
 */
public interface SaveScheduledEventPort {

    ScheduledEvent save(ScheduledEvent event);
}
