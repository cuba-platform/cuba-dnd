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


import com.haulmont.addon.dnd.components.defaulthandlers.DefaultHorizontalDropHandler;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.DDHorizontalLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.TargetDetails;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;

import java.util.*;

public class WebDDHorizontalLayout extends WebDDAbstractOrderedLayout<fi.jasoft.dragdroplayouts.DDHorizontalLayout> implements DDHorizontalLayout, TargetConverter {

    protected DropHandler dropHandler;

    public WebDDHorizontalLayout() {
        component = new DDHorizontalLayoutImpl();
        setDropHandler(new DefaultHorizontalDropHandler());
    }

    @Override
    public ExpandDirection getExpandDirection() {
        return null;
    }

    @Override
    public void setDropHandler(DropHandler handler) {
        dropHandler = handler;
        component.setDropHandler(new com.vaadin.event.dd.DropHandler() {
            @Override
            public void drop(com.vaadin.event.dd.DragAndDropEvent event) {
                DropAndCriterionHandler.drop(dropHandler, event, WebDDHorizontalLayout.this);
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return DropAndCriterionHandler.getCriterion(dropHandler, WebDDHorizontalLayout.this);
            }
        });
    }

    @Override
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    @Override
    public TargetDetails translateDropTargetDetails(Map<String, Object> clientVariables) {
        return new DDHorizontalLayoutTargetDetails(clientVariables, this);
    }

    @Override
    public TargetDetails convertTargetDetails(com.vaadin.event.dd.TargetDetails targetDetails) {

        fi.jasoft.dragdroplayouts.DDHorizontalLayout.HorizontalLayoutTargetDetails details =
                (fi.jasoft.dragdroplayouts.DDHorizontalLayout.HorizontalLayoutTargetDetails) targetDetails;

        Map<String, Object> dataDetails = ParamsMap.of(
                Constants.DROP_DETAIL_MOUSE_EVENT, details.getData(Constants.DROP_DETAIL_MOUSE_EVENT),
                Constants.DROP_DETAIL_TO, details.getOverIndex(),
                Constants.DROP_DETAIL_HORIZONTAL_DROP_LOCATION, details.getDropLocation(),
                Constants.DROP_DETAIL_LAYOUT_COMPONENTS, ownComponents
        );
        return new DDHorizontalLayoutTargetDetails(dataDetails, this);
    }

    protected class DDHorizontalLayoutImpl extends fi.jasoft.dragdroplayouts.DDHorizontalLayout implements DraggedComponentWrapper {

        public DDHorizontalLayoutImpl() {
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
            return WebDDHorizontalLayout.this;
        }
    }
}