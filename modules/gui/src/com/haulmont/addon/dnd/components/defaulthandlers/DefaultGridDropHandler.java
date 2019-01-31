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
import com.haulmont.cuba.gui.components.ComponentContainer;

public class DefaultGridDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        DDGridLayoutTargetDetails details = (DDGridLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();
        Component component = transferable.getTransferableComponent();
        Component sourceLayout = transferable.getSourceComponent();
        Component over = details.getOverComponent();
        DDGridLayout targetLayout = (DDGridLayout) details.getTarget();
        int row = details.getOverRow();
        int col = details.getOverColumn();

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

        if (over == null) {
            if (sourceLayout instanceof ComponentContainer) {
                ((ComponentContainer) sourceLayout).remove(component);
            }
            targetLayout.add(component, col, row);
        }
    }

    @Override
    public AcceptCriterion getCriterion() {
        return AcceptCriterion.ACCEPT_ALL;
    }
}