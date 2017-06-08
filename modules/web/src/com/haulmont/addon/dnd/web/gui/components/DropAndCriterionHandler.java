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

import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.ServerSideCriterion;
import com.haulmont.cuba.gui.components.Component;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;

import java.util.*;

public final class DropAndCriterionHandler {

    public static void drop(DropHandler handler, DragAndDropEvent event, TargetConverter converter) {
        Transferable vTransferable = event.getTransferable();

        com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent dropEvent =
                new com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent(
                        getTransferable(vTransferable),
                        converter.convertTargetDetails(event.getTargetDetails())
                );
        if (vTransferable.getSourceComponent() instanceof DraggedComponentWrapper) {
            handler.drop(dropEvent);
        }
    }

    public static AcceptCriterion getCriterion(DropHandler handler, TargetConverter converter) {
        com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion accept = handler.getCriterion();
        if (accept == null) {
            throw new IllegalArgumentException("AcceptCriterion is null");
        }
        if (accept instanceof AcceptCriterionWrapper) {
            return ((AcceptCriterionWrapper) accept).getCriterionImpl();
        }
        if (accept instanceof ServerSideCriterion) {
            return new com.vaadin.event.dd.acceptcriteria.ServerSideCriterion() {
                @Override
                public boolean accept(com.vaadin.event.dd.DragAndDropEvent event) {
                    Transferable t = event.getTransferable();
                    com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent dropEvent =
                            new com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent(
                                    getTransferable(t),
                                    converter.convertTargetDetails(event.getTargetDetails())
                            );

                    if (t.getSourceComponent() instanceof DraggedComponentWrapper) {
                        return ((ServerSideCriterion) accept).accept(dropEvent);
                    }
                    return true;
                }
            };
        }
        if (accept == com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion.ACCEPT_ALL) {
            return com.vaadin.event.dd.acceptcriteria.AcceptAll.get();
        }
        throw new IllegalArgumentException("Not supported criterion");
    }

    private static com.haulmont.addon.dnd.components.dragevent.Transferable getTransferable(Transferable vTransferable){
        Component component = ((DraggedComponentWrapper) vTransferable.getSourceComponent()).getSourceComponent();
        Collection<String> collection = vTransferable.getDataFlavors();
        List<String> list = new ArrayList<>(collection);
        Map<String, Object> map = new HashMap<>();
        for (String key : list) {
            map.put(key, vTransferable.getData(key));
        }
        map.put(Constants.TRANSFERABLE_DETAIL_COMPONENT,
                ((DraggedComponentWrapper) vTransferable.getSourceComponent()).getDraggedComponent(vTransferable));
        return new LayoutBoundTransferable(component, map);
    }
}