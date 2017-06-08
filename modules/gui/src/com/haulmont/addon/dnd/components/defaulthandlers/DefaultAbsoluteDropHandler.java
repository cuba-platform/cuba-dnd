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
import com.haulmont.addon.dnd.components.enums.Unit;

public class DefaultAbsoluteDropHandler implements DropHandler {

    @Override
    public void drop(DragAndDropEvent event) {
        DDAbsoluteLayoutTargetDetails details = (DDAbsoluteLayoutTargetDetails) event.getTargetDetails();
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event.getTransferable();

        Component sourceLayout = transferable.getSourceComponent();
        DDAbsoluteLayout targetLayout = (DDAbsoluteLayout) details.getTarget();
        Component component = transferable.getTransferableComponent();

        if (component == null) {
            return;
        }

        int leftPixelPosition = details.getRelativeLeft();
        int topPixelPosition = details.getRelativeTop();

        if (sourceLayout == targetLayout) {
            ComponentPosition position = targetLayout.getPosition(component);
            position.setLeft((float) leftPixelPosition, Unit.PIXELS);
            position.setTop((float) topPixelPosition, Unit.PIXELS);
            targetLayout.setPosition(component, position);
        } else {
            if (sourceLayout instanceof Component.Container) {
                ((Component.Container) sourceLayout).remove(component);
            }
            ComponentPosition position = new ComponentPosition();
            position.setLeft((float) leftPixelPosition, Unit.PIXELS);
            position.setTop((float) topPixelPosition, Unit.PIXELS);
            targetLayout.addComponent(component, position.getCSSString());
        }
    }

    @Override
    public AcceptCriterion getCriterion() {
        return AcceptCriterion.ACCEPT_ALL;
    }
}