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

package com.haulmont.addon.dnd.components;

import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.MouseEventDetails;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.addon.dnd.components.dragevent.TransferableImpl;

import java.util.Map;

public class LayoutBoundTransferable extends TransferableImpl {

    public LayoutBoundTransferable(Component sourceComponent, Map<String, Object> rawVariables) {
        super(sourceComponent, rawVariables);
    }

    public Component getTransferableComponent() {
        return (Component) getData(Constants.TRANSFERABLE_DETAIL_COMPONENT);
    }

    public MouseEventDetails getMouseDownEvent() {
        return MouseEventDetails.deSerialize(
                String.valueOf(getData(Constants.TRANSFERABLE_DETAIL_MOUSEDOWN)));
    }
}