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

import com.haulmont.addon.dnd.components.defaulthandlers.DefaultGridDropHandler;
import com.haulmont.bali.events.Subscription;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.MouseEventDetails;
import com.haulmont.cuba.gui.components.ShortcutAction;
import com.haulmont.cuba.gui.components.sys.FrameImplementation;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.gui.components.WebWrapperUtils;
import com.haulmont.addon.dnd.components.DDGridLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.haulmont.addon.dnd.components.dragfilter.DragFilter;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.addon.dnd.components.DDGridLayout;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.MarginInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class WebDDGridLayout extends WebAbstractComponent<com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDGridLayout> implements DDGridLayout, TargetConverter {

    protected List<Component> ownComponents = new ArrayList<>();
    protected LayoutEvents.LayoutClickListener layoutClickListener;

    protected DragFilter dragFilter = DragFilter.ALL;
    protected DropHandler dropHandler;

    public WebDDGridLayout() {
        component = new WebDDGridLayoutImpl();
        setDropHandler(new DefaultGridDropHandler());
    }

    @Override
    public void add(Component childComponent) {
        if (childComponent.getParent() != null && childComponent.getParent() != this) {
            throw new IllegalStateException("Component already has parent");
        }
        final com.vaadin.ui.Component vComponent = WebComponentsHelper.getComposition(childComponent);
        component.addComponent(vComponent);
        component.setComponentAlignment(vComponent, WebWrapperUtils.toVaadinAlignment(childComponent.getAlignment()));
        if (frame != null) {
            if (childComponent instanceof BelongToFrame
                    && ((BelongToFrame) childComponent).getFrame() == null) {
                ((BelongToFrame) childComponent).setFrame(frame);
            } else if (frame instanceof FrameImplementation) {
                ((FrameImplementation) frame).registerComponent(childComponent);
            }
        }
        ownComponents.add(childComponent);
        childComponent.setParent(this);
    }

    @Override
    public float getColumnExpandRatio(int col) {
        return component.getColumnExpandRatio(col);
    }

    @Override
    public void setColumnExpandRatio(int col, float ratio) {
        component.setColumnExpandRatio(col, ratio);
    }

    @Override
    public float getRowExpandRatio(int row) {
        return component.getRowExpandRatio(row);
    }

    @Override
    public void setRowExpandRatio(int row, float ratio) {
        component.setRowExpandRatio(row, ratio);
    }

    @Override
    public void add(Component component, int col, int row) {
        add(component, col, row, col, row);
    }

    @Override
    public void add(Component childComponent, int col, int row, int col2, int row2) {
        if (childComponent.getParent() != null && childComponent.getParent() != this) {
            throw new IllegalStateException("Component already has parent");
        }
        final com.vaadin.ui.Component vComponent = WebComponentsHelper.getComposition(childComponent);

        component.addComponent(vComponent, col, row, col2, row2);
        component.setComponentAlignment(vComponent, WebWrapperUtils.toVaadinAlignment(childComponent.getAlignment()));

        if (frame != null) {
            if (childComponent instanceof BelongToFrame
                    && ((BelongToFrame) childComponent).getFrame() == null) {
                ((BelongToFrame) childComponent).setFrame(frame);
            } else if (frame instanceof FrameImplementation) {
                ((FrameImplementation) frame).registerComponent(childComponent);
            }
        }
        ownComponents.add(childComponent);
        childComponent.setParent(this);
    }

    @Override
    public int getRows() {
        return component.getRows();
    }

    @Override
    public void setRows(int rows) {
        component.setRows(rows);
    }

    @Override
    public int getColumns() {
        return component.getColumns();
    }

    @Override
    public void setColumns(int columns) {
        component.setColumns(columns);
    }

    @Override
    public void remove(Component childComponent) {
        component.removeComponent(WebComponentsHelper.getComposition(childComponent));
        ownComponents.remove(childComponent);
        childComponent.setParent(null);
    }

    @Override
    public void removeAll() {
        component.removeAllComponents();

        Component[] components = ownComponents.toArray(new Component[ownComponents.size()]);
        ownComponents.clear();
        for (Component childComponent : components) {
            childComponent.setParent(null);
        }
    }

    @Override
    public Stream<Component> getOwnComponentsStream() {
        return ownComponents.stream();
    }

    @Override
    public void setFrame(Frame frame) {
        super.setFrame(frame);

        if (frame != null) {
            for (Component childComponent : ownComponents) {
                if (childComponent instanceof BelongToFrame
                        && ((BelongToFrame) childComponent).getFrame() == null) {
                    ((BelongToFrame) childComponent).setFrame(frame);
                }
            }
        }
    }

    @Override
    public Component getOwnComponent(String id) {
        Preconditions.checkNotNullArgument(id);

        return ownComponents.stream()
                .filter(component -> Objects.equals(id, component.getId()))
                .findFirst()
                .orElse(null);
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
        return Collections.unmodifiableCollection(ownComponents);
    }

    @Override
    public Collection<Component> getComponents() {
        return ComponentsHelper.getComponents(this);
    }

    @Override
    public void requestFocus() {
    }

    @Override
    public void setMargin(boolean enable) {
        component.setMargin(enable);
    }

    @Override
    public void setMargin(boolean topEnable, boolean rightEnable, boolean bottomEnable, boolean leftEnable) {
        component.setMargin(new MarginInfo(topEnable, rightEnable, bottomEnable, leftEnable));
    }

    @Override
    public void setSpacing(boolean enabled) {
        component.setSpacing(enabled);
    }

    @Override
    public Subscription addLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        if (layoutClickListener == null) {
            layoutClickListener = event -> {
                Component childComponent = findChildComponent(this, event.getChildComponent());
                Component clickedComponent = findChildComponent(this, event.getClickedComponent());
                MouseEventDetails mouseEventDetails = WebWrapperUtils.toMouseEventDetails(event);

                LayoutClickEvent layoutClickEvent =
                        new LayoutClickEvent(this, childComponent, clickedComponent, mouseEventDetails);

                publish(LayoutClickEvent.class, layoutClickEvent);
            };
            component.addLayoutClickListener(layoutClickListener);
        }

        return getEventHub().subscribe(LayoutClickEvent.class, listener);
    }

    protected Component findChildComponent(DDGridLayout layout, com.vaadin.ui.Component clickedComponent) {
        for (Component component : layout.getComponents()) {
            if (WebComponentsHelper.getComposition(component) == clickedComponent) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void removeLayoutClickListener(Consumer<LayoutClickEvent> listener) {
        getEventHub().unsubscribe(LayoutClickEvent.class, listener);
        if (layoutClickListener != null && !getEventHub().hasSubscriptions(LayoutClickEvent.class)) {
            component.removeLayoutClickListener(layoutClickListener);
            layoutClickListener = null;
        }
    }

    @Override
    public void setMargin(com.haulmont.cuba.gui.components.MarginInfo marginInfo) {
        throw new UnsupportedOperationException("Operation not supported yet");
    }

    @Override
    public com.haulmont.cuba.gui.components.MarginInfo getMargin() {
        throw new UnsupportedOperationException("Operation not supported yet");
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
    public boolean getSpacing() {
        return component.isSpacing();
    }

    @Override
    public DragFilter getDragFilter() {
        return dragFilter;
    }

    @Override
    public void setDragFilter(DragFilter dragFilter) {
        this.dragFilter = dragFilter;
        component.setDragFilter(component -> {
            Component componentToCheck = null;
            for (Component child : ownComponents) {
                if (component == child.unwrap(com.vaadin.ui.Component.class)) {
                    componentToCheck = child;
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
                DropAndCriterionHandler.drop(dropHandler, event, WebDDGridLayout.this);
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return DropAndCriterionHandler.getCriterion(dropHandler, WebDDGridLayout.this);
            }
        });
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new DDGridLayoutTargetDetails(clientVariables, this);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails targetDetails) {
        com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDGridLayout.GridLayoutTargetDetails details =
                (com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDGridLayout.GridLayoutTargetDetails) targetDetails;

        Map<String, Object> dataDetails = new HashMap<>();
        dataDetails.put(Constants.DROP_DETAIL_MOUSE_EVENT, details.getData(Constants.DROP_DETAIL_MOUSE_EVENT));
        dataDetails.put(Constants.DROP_DETAIL_OVER_CLASS, details.getData(Constants.DROP_DETAIL_OVER_CLASS));
        dataDetails.put(Constants.DROP_DETAIL_COLUMN, details.getOverColumn());
        dataDetails.put(Constants.DROP_DETAIL_ROW, details.getOverRow());
        dataDetails.put(Constants.DROP_DETAIL_HORIZONTAL_DROP_LOCATION, details.getHorizontalDropLocation());
        dataDetails.put(Constants.DROP_DETAIL_EMPTY_CELL, details.overEmptyCell());
        dataDetails.put(Constants.DROP_DETAIL_VERTICAL_DROP_LOCATION, details.getVerticalDropLocation());

        com.vaadin.ui.Component overComponent = component.getComponent(details.getOverColumn(), details.getOverRow());
        Component componentToAdd = null;
        for (Component ownComponent : ownComponents) {
            if (ownComponent.unwrap(com.vaadin.ui.Component.class) == overComponent) {
                componentToAdd = ownComponent;
                break;
            }
        }
        dataDetails.put(Constants.DROP_DETAIL_GRID_OVER_COMPONENT, componentToAdd);
        return new DDGridLayoutTargetDetails(dataDetails, this);
    }

    @Nullable
    @Override
    public Component getComponent(int columnIndex, int rowIndex) {
        com.vaadin.ui.Component vComponent = this.component.getComponent(columnIndex, rowIndex);
        return findChildComponent(this, vComponent);
    }

    @Nullable
    @Override
    public Area getComponentArea(Component childComponent) {
        com.vaadin.ui.Component vComponent = childComponent.unwrapComposition(com.vaadin.ui.Component.class);
        com.vaadin.ui.GridLayout.Area vArea = component.getComponentArea(vComponent);
        if (vArea == null) {
            return null;
        }

        return new Area(childComponent,
                vArea.getColumn1(), vArea.getRow1(),
                vArea.getColumn2(), vArea.getRow2());
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return component.isRequiredIndicatorVisible();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean visible) {
        component.setRequiredIndicatorVisible(visible);
    }

    protected class WebDDGridLayoutImpl extends com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDGridLayout implements DraggedComponentWrapper {

        public WebDDGridLayoutImpl() {
        }

        @Override
        public Component getDraggedComponent(Transferable t) {
            com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDGridLayout.GridLayoutTransferable tr =
                    (com.haulmont.cuba.web.widgets.addons.dragdroplayouts.DDGridLayout.GridLayoutTransferable) t;
            for (Component component : ownComponents) {
                if (tr.getComponent() == component.unwrap(com.vaadin.ui.Component.class)) {
                    return component;
                }
            }
            return null;
        }

        @Override
        public Component getSourceComponent() {
            return WebDDGridLayout.this;
        }
    }
}