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

import com.haulmont.addon.dnd.components.dragevent.DropTarget;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentContainer;

public interface DragAndDropWrapper extends ComponentContainer, Component.HasCaption, Component.HasIcon, DropTarget {
    String NAME = "dndWrapper";

    void setDragStartMode(DragStartMode startMode);
    DragStartMode getDragStartMode();

    void setDropHandler(DropHandler handler);
    DropHandler getDropHandler();

    void setDraggedComponent(Component component);
    Component getDraggedComponent();

    enum DragStartMode {
        NONE,
        COMPONENT,
        COMPONENT_OTHER,
        WRAPPER,
        HTML5,
    }
}