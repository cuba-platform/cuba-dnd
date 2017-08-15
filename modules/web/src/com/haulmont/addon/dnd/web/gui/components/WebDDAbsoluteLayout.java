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

import com.haulmont.addon.dnd.components.defaulthandlers.DefaultAbsoluteDropHandler;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.addon.dnd.components.ComponentPosition;
import com.haulmont.addon.dnd.components.DDAbsoluteLayout;
import com.haulmont.addon.dnd.components.DDAbsoluteLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.components.dragfilter.DragFilter;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import fi.jasoft.dragdroplayouts.details.AbsoluteLayoutTargetDetails;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class WebDDAbsoluteLayout extends WebAbstractComponent<fi.jasoft.dragdroplayouts.DDAbsoluteLayout> implements DDAbsoluteLayout, TargetConverter {

    protected Map<Component, ComponentPosition> components = new WeakHashMap<>();
    protected DragFilter dragFilter = DragFilter.ALL;
    protected DropHandler dropHandler;

    public WebDDAbsoluteLayout() {
        component = new WebDDAbsoluteLayoutImpl();
        setDropHandler(new DefaultAbsoluteDropHandler());
    }

    @Override
    public void add(Component childComponent) {
        components.put(childComponent, new ComponentPosition());
        component.addComponent(childComponent.unwrap(com.vaadin.ui.Component.class));
    }

    @Override
    public void addComponent(Component component, String cssPosition) {
        ComponentPosition position = new ComponentPosition();
        position.setCSSString(cssPosition);
        components.put(component, position);
        this.component.addComponent(component.unwrap(com.vaadin.ui.Component.class), cssPosition);
    }

    @Override
    public ComponentPosition getPosition(Component component) {
        return components.get(component);
    }

    @Override
    public void setPosition(Component component, ComponentPosition position) {
        if (components.containsKey(component)) {
            this.component.removeComponent(component.unwrap(com.vaadin.ui.Component.class));
            this.component.addComponent(component.unwrap(com.vaadin.ui.Component.class), position.getCSSString());
            components.replace(component, position);
        }
    }

    @Override
    public void remove(Component childComponent) {
        if (components.containsKey(childComponent)) {
            components.remove(childComponent);
            component.removeComponent(childComponent.unwrap(com.vaadin.ui.Component.class));
        }
    }

    @Override
    public void removeAll() {
        components.clear();
    }

    @Nullable
    @Override
    public Component getOwnComponent(String id) {
        Preconditions.checkNotNullArgument(id);
        Set<Component> set = components.keySet();
        for (Component component : set) {
            if (component.getId() != null) {
                if (component.getId().equals(id)) {
                    return component;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Component getComponent(String id) {
        return ComponentsHelper.getComponent(this, id);
    }

    @Nonnull
    @Override
    public Component getComponentNN(String id) {
        Component component = getComponent(id);
        if (component == null) {
            throw new IllegalArgumentException(String.format("Not found component with id '%s'", id));
        }
        return component;
    }

    @Override
    public Collection<Component> getOwnComponents() {
        return components.keySet();
    }

    @Override
    public Collection<Component> getComponents() {
        return ComponentsHelper.getComponents(this);
    }

    @Override
    public DragFilter getDragFilter() {
        return dragFilter;
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        this.dragFilter = dragFilter;
        component.setDragFilter(new fi.jasoft.dragdroplayouts.interfaces.DragFilter() {
            @Override
            public boolean isDraggable(com.vaadin.ui.Component component) {
                Set<Component> set = components.keySet();
                Iterator<Component> iterator = set.iterator();
                Component componentToCheck = null;
                while (iterator.hasNext()) {
                    componentToCheck = iterator.next();
                    if (componentToCheck.unwrap(com.vaadin.ui.Component.class) == component) {
                        break;
                    }
                }
                return dragFilter.isDraggable(componentToCheck);
            }
        });
    }

    @Override
    public void setDragMode(LayoutDragMode startMode) {
        switch (startMode) {
            case NONE:
                component.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.NONE);
                break;
            case CLONE:
                component.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE);
                break;
            case CLONE_OTHER:
                component.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CLONE_OTHER);
                break;
            case CAPTION:
                component.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.CAPTION);
                break;
            default:
                component.setDragMode(fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode.NONE);
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
    public void setDropHandler(DropHandler handler) {
        dropHandler = handler;
        component.setDropHandler(new com.vaadin.event.dd.DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                DropAndCriterionHandler.drop(dropHandler, event, WebDDAbsoluteLayout.this);
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return DropAndCriterionHandler.getCriterion(dropHandler, WebDDAbsoluteLayout.this);
            }
        });
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new DDAbsoluteLayoutTargetDetails(this, clientVariables);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails targetDetails) {
        AbsoluteLayoutTargetDetails details = (AbsoluteLayoutTargetDetails) targetDetails;

        Map<String, Object> dataDetails = new HashMap<>();

        dataDetails.put(Constants.DROP_DETAIL_ABSOLUTE_TOP, details.getAbsoluteTop());
        dataDetails.put(Constants.DROP_DETAIL_MOUSE_EVENT, details.getData(Constants.DROP_DETAIL_MOUSE_EVENT));
        dataDetails.put(Constants.DROP_DETAIL_RELATIVE_LEFT, details.getRelativeLeft());
        dataDetails.put(Constants.DROP_DETAIL_RELATIVE_TOP, details.getRelativeTop());
        dataDetails.put(Constants.DROP_DETAIL_COMPONENT_HEIGHT, details.getComponentHeight());
        dataDetails.put(Constants.DROP_DETAIL_ABSOLUTE_LEFT, details.getAbsoluteLeft());
        dataDetails.put(Constants.DROP_DETAIL_COMPONENT_WIDTH, details.getComponentWidth());

        return new DDAbsoluteLayoutTargetDetails(this, dataDetails);
    }

    protected class WebDDAbsoluteLayoutImpl extends fi.jasoft.dragdroplayouts.DDAbsoluteLayout implements DraggedComponentWrapper {

        @Override
        public Component getDraggedComponent(Transferable t) {
            LayoutBoundTransferable tr = (LayoutBoundTransferable) t;
            Set<Component> set = components.keySet();
            Iterator<Component> iterator = set.iterator();
            while (iterator.hasNext()) {
                Component componentToCheck = iterator.next();
                if (componentToCheck.unwrap(com.vaadin.ui.Component.class) == tr.getComponent()) {
                    return componentToCheck;
                }
            }
            return null;
        }

        @Override
        public Component getSourceComponent() {
            return WebDDAbsoluteLayout.this;
        }
    }
}