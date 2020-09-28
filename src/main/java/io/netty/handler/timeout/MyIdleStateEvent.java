/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.timeout;

import io.netty.channel.Channel;
import io.netty.util.internal.ObjectUtil;
import lombok.ToString;

/**
 * A user event triggered by {@link IdleStateHandler} when a {@link Channel} is idle.
 */
@ToString
public class MyIdleStateEvent {
    public static final MyIdleStateEvent FIRST_READER_IDLE_STATE_EVENT = new MyIdleStateEvent(IdleState.READER_IDLE, true);
    public static final MyIdleStateEvent READER_IDLE_STATE_EVENT = new MyIdleStateEvent(IdleState.READER_IDLE, false);
    public static final MyIdleStateEvent FIRST_WRITER_IDLE_STATE_EVENT = new MyIdleStateEvent(IdleState.WRITER_IDLE, true);
    public static final MyIdleStateEvent WRITER_IDLE_STATE_EVENT = new MyIdleStateEvent(IdleState.WRITER_IDLE, false);
    public static final MyIdleStateEvent FIRST_ALL_IDLE_STATE_EVENT = new MyIdleStateEvent(IdleState.ALL_IDLE, true);
    public static final MyIdleStateEvent ALL_IDLE_STATE_EVENT = new MyIdleStateEvent(IdleState.ALL_IDLE, false);

    private final IdleState state;
    private final boolean first;

    /**
     * Constructor for sub-classes.
     *
     * @param state the {@link MyIdleStateEvent} which triggered the event.
     * @param first {@code true} if its the first idle event for the {@link MyIdleStateEvent}.
     */
    protected MyIdleStateEvent(IdleState state, boolean first) {
        this.state = ObjectUtil.checkNotNull(state, "state");
        this.first = first;
    }

    /**
     * Returns the idle state.
     */
    public IdleState state() {
        return state;
    }

    /**
     * Returns {@code true} if this was the first event for the {@link IdleState}
     */
    public boolean isFirst() {
        return first;
    }
}
