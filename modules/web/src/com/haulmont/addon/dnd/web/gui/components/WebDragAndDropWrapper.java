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

import com.haulmont.addon.dnd.components.defaulthandlers.DefaultWrapperDropHandler;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.addon.dnd.components.DragAndDropWrapperTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.DragAndDropWrapper;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.web.toolkit.ui.CubaDragAndDropWrapper;
import com.vaadin.event.Transferable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class WebDragAndDropWrapper extends WebAbstractComponent<CubaDragAndDropWrapper> implements DragAndDropWrapper, TargetConverter {

    protected Component childComponent;
    protected DropHandler dropHandler;

    public WebDragAndDropWrapper() {
        component = new DragAndDropWrapperImpl();
        setDropHandler(new DefaultWrapperDropHandler());
    }

    @Override
    public void add(Component childComponent) {
        Preconditions.checkNotNullArgument(childComponent);
        if ((this.childComponent == null) || (!this.childComponent.equals(childComponent))) {
            this.childComponent = childComponent;
            component.setCompositionRoot(childComponent.unwrap(com.vaadin.ui.Component.class));
        } else {
            throw new IllegalStateException("Component has already added");
        }
    }

    @Override
    public void remove(Component childComponent) {
        if (this.childComponent != null) {
            if (this.childComponent.equals(childComponent)) {
                this.childComponent = null;
                component.setCompositionRoot(null);
            }
        }
    }

    @Override
    public void setDraggedComponent(Component draggedComponent) {
        childComponent = draggedComponent;
        if (childComponent == null) {
            component.setCompositionRoot(null);
        } else {
            component.setCompositionRoot(childComponent.unwrap(com.vaadin.ui.Component.class));
        }
    }

    @Override
    public Component getDraggedComponent() {
        return childComponent;
    }

    @Override
    public void removeAll() {
        component.setCompositionRoot(null);
        childComponent = null;
    }

    @Override
    public void setDropHandler(DropHandler handler) {
        dropHandler = handler;
        component.setDropHandler(new com.vaadin.event.dd.DropHandler() {
            @Override
            public void drop(com.vaadin.event.dd.DragAndDropEvent event) {
                DropAndCriterionHandler.drop(dropHandler, event, WebDragAndDropWrapper.this);
            }

            @Override
            public com.vaadin.event.dd.acceptcriteria.AcceptCriterion getAcceptCriterion() {
                return DropAndCriterionHandler.getCriterion(dropHandler, WebDragAndDropWrapper.this);
            }
        });
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Nullable
    @Override
    public Component getOwnComponent(String id) {
        Preconditions.checkNotNullArgument(id);
        if (childComponent != null && childComponent.getId() != null) {
            if (childComponent.getId().equals(id)) {
                return childComponent;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Component getComponent(String id) {
        Preconditions.checkNotNullArgument(id);
        if (childComponent != null && childComponent.getId() != null) {
            if (childComponent.getId().equals(id)) {
                return childComponent;
            }
        }
        return null;
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
        if (childComponent != null) {
            return Collections.singletonList(childComponent);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Collection<Component> getComponents() {
        return getOwnComponents();
    }

    @Override
    public void setDragStartMode(DragAndDropWrapper.DragStartMode startMode) {
        switch (startMode) {
            case NONE:
                component.setDragStartMode(com.vaadin.ui.DragAndDropWrapper.DragStartMode.NONE);
                break;
            case COMPONENT:
                component.setDragStartMode(com.vaadin.ui.DragAndDropWrapper.DragStartMode.COMPONENT);
                break;
            case COMPONENT_OTHER:
                component.setDragStartMode(com.vaadin.ui.DragAndDropWrapper.DragStartMode.COMPONENT_OTHER);
                break;
            case WRAPPER:
                component.setDragStartMode(com.vaadin.ui.DragAndDropWrapper.DragStartMode.WRAPPER);
                break;
            case HTML5:
                component.setDragStartMode(com.vaadin.ui.DragAndDropWrapper.DragStartMode.HTML5);
                break;
        }
    }

    @Override
    public DragStartMode getDragStartMode() {
        com.vaadin.ui.DragAndDropWrapper.DragStartMode startMode = this.component.getDragStartMode();
        switch (startMode) {
            case NONE:
                return DragStartMode.NONE;
            case COMPONENT:
                return DragStartMode.COMPONENT;
            case COMPONENT_OTHER:
                return DragStartMode.COMPONENT_OTHER;
            case WRAPPER:
                return DragStartMode.WRAPPER;
            case HTML5:
                return DragStartMode.HTML5;
            default:
                return DragStartMode.NONE;
        }
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new DragAndDropWrapperTargetDetails(clientVariables, this);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails targetDetails) {
        com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails details =
                (com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails) targetDetails;

        Map<String, Object> dataDetails = ParamsMap.of(
                Constants.DROP_DETAIL_ABSOLUTE_TOP, details.getAbsoluteTop(),
                Constants.DROP_DETAIL_ABSOLUTE_LEFT, details.getAbsoluteLeft(),
                Constants.DROP_DETAIL_MOUSE_EVENT, details.getData(Constants.DROP_DETAIL_MOUSE_EVENT),
                Constants.DROP_DETAIL_VERTICAL_DROP_LOCATION, details.getVerticalDropLocation(),
                Constants.DROP_DETAIL_HORIZONTAL_DROP_LOCATION, details.getHorizontalDropLocation()
        );
        return new DragAndDropWrapperTargetDetails(dataDetails, this);
    }

    protected class DragAndDropWrapperImpl extends CubaDragAndDropWrapper implements DraggedComponentWrapper {

        public DragAndDropWrapperImpl() {
        }

        @Override
        public Component getDraggedComponent(Transferable t) {
            return childComponent;
        }

        @Override
        public Component getSourceComponent() {
            return WebDragAndDropWrapper.this;
        }
    }
}