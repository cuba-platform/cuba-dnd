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

package com.haulmont.addon.dnd.components.defaulthandlers;

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.addon.dnd.components.*;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.ComponentContainer;

public class DefaultVerticalDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        DDVerticalLayoutTargetDetails details = (DDVerticalLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable t = (LayoutBoundTransferable) event.getTransferable();

        Component component = t.getTransferableComponent();
        DDVerticalLayout targetLayout = (DDVerticalLayout) details.getTarget();

        Component sourceLayout = t.getSourceComponent();

        if (component == null) {
            return;
        }

        Component parent = targetLayout;
        while (parent != null) {
            if (parent == component) {
                return;
            }
            parent = parent.getParent();
        }

        int indexTo = details.getOverIndex();
        int indexFrom = targetLayout.indexOf(component);

        if (sourceLayout == targetLayout) {
            if (indexFrom == indexTo) {
                return;
            }
            targetLayout.remove(component);
            if (indexTo > indexFrom) {
                indexTo--;
            }
            VerticalDropLocation loc = details.getDropLocation();
            if (loc == VerticalDropLocation.MIDDLE
                    || loc == VerticalDropLocation.BOTTOM) {
                indexTo++;
            }
            if (indexTo >= 0) {
                targetLayout.add(component, indexTo);
            } else {
                targetLayout.add(component);
            }
        } else {
            if (sourceLayout instanceof ComponentContainer) {
                ((ComponentContainer) sourceLayout).remove(component);
            }
            VerticalDropLocation loc = details.getDropLocation();
            if (loc == VerticalDropLocation.MIDDLE
                    || loc == VerticalDropLocation.BOTTOM) {
                indexTo++;
            }
            if (indexTo >= 0) {
                targetLayout.add(component, indexTo);
            } else {
                targetLayout.add(component);
            }
        }
    }

    @Override
    public AcceptCriterion getCriterion() {
        return AcceptCriterion.ACCEPT_ALL;
    }
}