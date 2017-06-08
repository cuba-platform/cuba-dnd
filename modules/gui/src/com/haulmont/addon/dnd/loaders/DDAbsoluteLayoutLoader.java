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

package com.haulmont.addon.dnd.loaders;

import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.addon.dnd.components.DDAbsoluteLayout;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.loaders.ContainerLoader;


public class DDAbsoluteLayoutLoader extends ContainerLoader<DDAbsoluteLayout> {

    @Override
    public void createComponent() {
        resultComponent = factory.createComponent(DDAbsoluteLayout.class);
        loadId(resultComponent, element);
        createSubComponents(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        String caption = element.attributeValue("caption");
        if (caption != null) {
            resultComponent.setCaption(caption);
        }
        String width = element.attributeValue("width");
        if (width != null) {
            resultComponent.setWidth(width);
        }
        String height = element.attributeValue("height");
        if (height != null) {
            resultComponent.setHeight(height);
        }
        String description = element.attributeValue("description");
        if (description != null) {
            resultComponent.setDescription(description);
        }
        String visible = element.attributeValue("visible");
        if (visible != null) {
            resultComponent.setVisible(Boolean.getBoolean(visible));
        }
        String align = element.attributeValue("align");
        if (align != null) {
            resultComponent.setAlignment(Component.Alignment.valueOf(align));
        }
        String enable = element.attributeValue("enable");
        if (enable != null) {
            resultComponent.setEnabled(Boolean.getBoolean(enable));
        }
        String responsive = element.attributeValue("responsive");
        if (responsive != null) {
            resultComponent.setResponsive(Boolean.getBoolean(responsive));
        }
        String styleName = element.attributeValue("stylename");
        if (styleName != null) {
            resultComponent.setStyleName(styleName);
        }
        String icon = element.attributeValue("icon");
        if (icon != null) {
            resultComponent.setIcon(icon);
        }
        String dragMode = element.attributeValue("dragMode");
        if (dragMode != null) {
            resultComponent.setDragMode(LayoutDragMode.valueOf(dragMode));
        }
        loadSubComponents();
    }
}