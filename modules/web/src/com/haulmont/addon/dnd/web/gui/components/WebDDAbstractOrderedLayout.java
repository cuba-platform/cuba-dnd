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

package com.haulmont.addon.dnd.web.gui.components;

import com.haulmont.cuba.web.gui.components.WebAbstractBox;
import com.haulmont.addon.dnd.components.DDLayout;
import com.haulmont.addon.dnd.components.dragfilter.DragFilter;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.haulmont.cuba.web.widgets.addons.dragdroplayouts.interfaces.DragFilterSupport;
import com.haulmont.cuba.web.widgets.addons.dragdroplayouts.interfaces.LayoutDragSource;

import java.util.ArrayList;
import java.util.List;

public abstract class WebDDAbstractOrderedLayout<T extends AbstractOrderedLayout & LayoutDragSource & DragFilterSupport>
        extends WebAbstractBox<T> implements DDLayout, com.haulmont.addon.dnd.components.dragfilter.DragFilterSupport {

    protected DragFilter dragFilter = DragFilter.ALL;

    public WebDDAbstractOrderedLayout() {
    }

    @Override
    public void setDragMode(LayoutDragMode startMode) {
        switch (startMode) {
            case NONE:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.NONE);
                break;
            case CLONE:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.CLONE);
                break;
            case CAPTION:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.CAPTION);
                break;
            case CLONE_OTHER:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.CLONE_OTHER);
                break;
            default:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.NONE);
        }
    }

    @Override
    public LayoutDragMode getDragMode() {
        switch (component.getDragMode()) {
            case NONE:
                return LayoutDragMode.NONE;
            case CLONE:
                return LayoutDragMode.CLONE;
            case CLONE_OTHER:
                return LayoutDragMode.CLONE_OTHER;
            case CAPTION:
                return LayoutDragMode.CAPTION;
            default:
                return LayoutDragMode.NONE;
        }
    }

    @Override
    public DragFilter getDragFilter() {
        return dragFilter;
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        this.dragFilter = dragFilter;
        component.setDragFilter(component -> {
            com.haulmont.cuba.gui.components.Component componentToCheck = null;
            List<com.haulmont.cuba.gui.components.Component> list = new ArrayList<>(getComponents());
            for (com.haulmont.cuba.gui.components.Component child : list) {
                if (component == child.unwrap(Component.class)) {
                    componentToCheck = child;
                    break;
                }
            }
            return dragFilter.isDraggable(componentToCheck);
        });
    }
}