/*
 * Copyright (c) 2008-2017 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.haulmont.addon.dnd.components.dragevent;

import java.util.HashMap;
import java.util.Map;

public class TargetDetailsImpl implements TargetDetails {

    private HashMap<String, Object> data = new HashMap<>();
    private DropTarget dropTarget;

    protected TargetDetailsImpl(Map<String, Object> rawDropData) {
        data.putAll(rawDropData);
    }

    public TargetDetailsImpl(Map<String, Object> rawDropData,
                             DropTarget dropTarget) {
        this(rawDropData);
        this.dropTarget = dropTarget;
    }

    public MouseEventDetails getMouseEvent() {
        return MouseEventDetails.deSerialize(
                String.valueOf(getData(Constants.DROP_DETAIL_MOUSE_EVENT)));
    }

    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    public Object setData(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public DropTarget getTarget() {
        return dropTarget;
    }
}