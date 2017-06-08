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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.addon.dnd.components.DDVerticalLayout;
import com.haulmont.addon.dnd.components.DDVerticalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;

import java.util.*;

public class WebDDVerticalLayout extends WebDDAbstractOrderedLayout<fi.jasoft.dragdroplayouts.DDVerticalLayout> implements DDVerticalLayout, TargetConverter {

    private DropHandler dropHandler;

    public WebDDVerticalLayout() {
        component = new DDVerticalLayoutImpl();
    }

    @Override
    public ExpandDirection getExpandDirection() {
        return null;
    }

    @Override
    public void setDropHandler(DropHandler handler) {
        this.dropHandler = handler;
        component.setDropHandler(new com.vaadin.event.dd.DropHandler() {
            @Override
            public void drop(com.vaadin.event.dd.DragAndDropEvent event) {
                DropAndCriterionHandler.drop(dropHandler, event, WebDDVerticalLayout.this);
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return DropAndCriterionHandler.getCriterion(dropHandler, WebDDVerticalLayout.this);
            }
        });
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new DDVerticalLayoutTargetDetails(clientVariables, this);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails targetDetails) {
        fi.jasoft.dragdroplayouts.DDVerticalLayout.VerticalLayoutTargetDetails details =
                (fi.jasoft.dragdroplayouts.DDVerticalLayout.VerticalLayoutTargetDetails) targetDetails;

        Map<String, Object> dataDetails = ParamsMap.of(
                Constants.DROP_DETAIL_MOUSE_EVENT, details.getData(Constants.DROP_DETAIL_MOUSE_EVENT),
                Constants.DROP_DETAIL_TO, details.getOverIndex(),
                Constants.DROP_DETAIL_VERTICAL_DROP_LOCATION, details.getDropLocation(),
                Constants.DROP_DETAIL_LAYOUT_COMPONENTS, ownComponents
        );

        return new DDVerticalLayoutTargetDetails(dataDetails, this);
    }

    protected class DDVerticalLayoutImpl extends fi.jasoft.dragdroplayouts.DDVerticalLayout implements DraggedComponentWrapper {

        public DDVerticalLayoutImpl() {
        }

        @Override
        public Component getDraggedComponent(Transferable t) {
            fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable tr = (fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable) t;
            List<Component> list = new ArrayList<>(getComponents());
            for (Component child : list) {
                if (tr.getComponent() == child.unwrap(com.vaadin.ui.Component.class)) {
                    return child;
                }
            }
            return null;
        }

        @Override
        public Component getSourceComponent() {
            return WebDDVerticalLayout.this;
        }
    }
}