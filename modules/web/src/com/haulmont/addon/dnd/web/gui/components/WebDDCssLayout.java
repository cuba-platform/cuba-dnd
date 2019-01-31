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

import com.haulmont.addon.dnd.components.defaulthandlers.DefaultCssDropHandler;
import com.haulmont.bali.events.Subscription;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ShortcutAction;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.addon.dnd.components.DDCssLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.components.dragfilter.DragFilter;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDCssLayout;
import com.haulmont.cuba.web.widgets.addons.dragdroplayouts.events.LayoutBoundTransferable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class WebDDCssLayout extends WebAbstractComponent<DDCssLayout> implements com.haulmont.addon.dnd.components.DDCssLayout, TargetConverter {

    protected LinkedList<Component> ownComponents = new LinkedList<>();
    protected DragFilter dragFilter = DragFilter.ALL;
    protected DropHandler dropHandler;

    public WebDDCssLayout() {
        component = new WebDDCssLayoutImpl();
        setDropHandler(new DefaultCssDropHandler());
    }

    @Override
    public void add(Component childComponent, int index) {
        ownComponents.add(index, childComponent);
        component.addComponent(childComponent.unwrap(com.vaadin.ui.Component.class), index);
    }

    @Override
    public int indexOf(Component component) {
        return ownComponents.indexOf(component);
    }

    @Override
    public void add(Component childComponent) {
        ownComponents.add(childComponent);
        component.addComponent(childComponent.unwrap(com.vaadin.ui.Component.class));
    }

    @Override
    public void remove(Component childComponent) {
        ownComponents.remove(childComponent);
        component.removeComponent(childComponent.unwrap(com.vaadin.ui.Component.class));
    }

    @Override
    public void removeAll() {
        ownComponents.clear();
        component.removeAllComponents();
    }

    @Nullable
    @Override
    public Component getOwnComponent(String id) {
        Preconditions.checkNotNullArgument(id);
        Iterator<Component> iterator = ownComponents.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            if (component.getId() != null) {
                if (component.getId().equals(id)) {
                    return component;
                }
            }
        }
        return null;
    }

    @Override
    public void addShortcutAction(ShortcutAction action) {
        throw new UnsupportedOperationException("Operation not supported yet");
    }

    @Override
    public void removeShortcutAction(ShortcutAction action) {
        throw new UnsupportedOperationException("Operation not supported yet");
    }

    @Override
    public Stream<Component> getOwnComponentsStream() {
        return ownComponents.stream();
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
        return ownComponents;
    }

    @Override
    public Collection<Component> getComponents() {
        return ComponentsHelper.getComponents(this);
    }

    @Override
    public Subscription addLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        throw new UnsupportedOperationException("Operation not supported yet");
    }

    @Override
    public void removeLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        throw new UnsupportedOperationException("Operation not supported yet");
    }

    @Override
    public DragFilter getDragFilter() {
        return dragFilter;
    }

    @Override
    public void setDragFilter(DragFilter filter) {
        this.dragFilter = filter;
        component.setDragFilter(component -> {
            Iterator<Component> iterator = ownComponents.iterator();
            Component componentToCheck = null;
            while (iterator.hasNext()) {
                componentToCheck = iterator.next();
                if (componentToCheck.unwrap(com.vaadin.ui.Component.class) == component) {
                    break;
                }
            }
            return dragFilter.isDraggable(componentToCheck);
        });
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
            case CLONE_OTHER:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.CLONE_OTHER);
                break;
            case CAPTION:
                component.setDragMode(com.haulmont.cuba.web.widgets.client.addons.dragdroplayouts.ui.LayoutDragMode.CAPTION);
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
    public void setDropHandler(DropHandler handler) {
        this.dropHandler = handler;
        component.setDropHandler(new com.vaadin.event.dd.DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                DropAndCriterionHandler.drop(dropHandler, event, WebDDCssLayout.this);
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return DropAndCriterionHandler.getCriterion(dropHandler, WebDDCssLayout.this);
            }
        });
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new DDCssLayoutTargetDetails(clientVariables, this);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails details) {
        DDCssLayout.CssLayoutTargetDetails targetDetails = (DDCssLayout.CssLayoutTargetDetails) details;

        Map<String, Object> dataDetails = ParamsMap.of(
                Constants.DROP_DETAIL_MOUSE_EVENT, details.getData(Constants.DROP_DETAIL_MOUSE_EVENT),
                Constants.DROP_DETAIL_TO, targetDetails.getOverIndex(),
                Constants.DROP_DETAIL_HORIZONTAL_DROP_LOCATION, targetDetails.getHorizontalDropLocation(),
                Constants.DROP_DETAIL_VERTICAL_DROP_LOCATION, targetDetails.getVerticalDropLocation(),
                Constants.DROP_DETAIL_LAYOUT_COMPONENTS, ownComponents
        );

        return new DDCssLayoutTargetDetails(dataDetails, this);
    }

    @Nullable
    @Override
    public Component getComponent(int index) {
        return ownComponents.get(index);
    }

    protected class WebDDCssLayoutImpl extends DDCssLayout implements DraggedComponentWrapper {

        public WebDDCssLayoutImpl() {
        }

        @Override
        public Component getDraggedComponent(Transferable t) {
            LayoutBoundTransferable tr = (LayoutBoundTransferable) t;
            Iterator<Component> iterator = ownComponents.iterator();
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
            return WebDDCssLayout.this;
        }
    }
}