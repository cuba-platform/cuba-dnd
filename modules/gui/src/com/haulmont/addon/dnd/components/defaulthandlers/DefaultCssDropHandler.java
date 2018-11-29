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

import com.haulmont.addon.dnd.components.DDCssLayout;
import com.haulmont.addon.dnd.components.DDCssLayoutTargetDetails;
import com.haulmont.addon.dnd.components.DropHandler;
import com.haulmont.addon.dnd.components.LayoutBoundTransferable;
import com.haulmont.addon.dnd.components.acceptcriterion.AcceptCriterion;
import com.haulmont.addon.dnd.components.dragevent.DragAndDropEvent;
import com.haulmont.addon.dnd.components.enums.HorizontalDropLocation;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ComponentContainer;

public class DefaultCssDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        DDCssLayoutTargetDetails details = (DDCssLayoutTargetDetails) event.getTargetDetails();

        Component sourceLayout = transferable.getSourceComponent();
        DDCssLayout targetLayout = (DDCssLayout) details.getTarget();
        Component component = transferable.getTransferableComponent();

        int overIndex = details.getOverIndex();
        Component over = details.getOverComponent();

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

        if (sourceLayout == targetLayout) {
            targetLayout.remove(component);
            if (overIndex >= 0 && overIndex < targetLayout.getComponents().size()) {
                targetLayout.add(component, overIndex);
            } else {
                targetLayout.add(component);
            }
        } else {
            HorizontalDropLocation hl = details.getHorizontalDropLocation();
            VerticalDropLocation vl = details.getVerticalDropLocation();

            if (over == null) {
                if (vl == VerticalDropLocation.TOP
                        || hl == HorizontalDropLocation.LEFT) {
                    overIndex = 0;
                } else if (vl == VerticalDropLocation.BOTTOM
                        || hl == HorizontalDropLocation.RIGHT) {
                    overIndex = -1;
                }
            } else {
                if (vl == VerticalDropLocation.BOTTOM
                        || hl == HorizontalDropLocation.RIGHT) {
                    overIndex++;
                }
            }

            if (sourceLayout instanceof ComponentContainer) {
                ((ComponentContainer) sourceLayout).remove(component);
            }
            if (overIndex >= 0 && overIndex < targetLayout.getComponents().size()) {
                targetLayout.add(component, overIndex);
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