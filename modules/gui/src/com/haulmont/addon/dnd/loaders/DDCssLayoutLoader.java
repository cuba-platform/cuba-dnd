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

import com.haulmont.addon.dnd.components.DDCssLayout;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.cuba.gui.components.CssLayout;
import com.haulmont.cuba.gui.xml.layout.loaders.ContainerLoader;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;


public class DDCssLayoutLoader extends ContainerLoader<DDCssLayout> {

    public DDCssLayoutLoader() {
    }

    public void createComponent() {
        resultComponent = factory.create(DDCssLayout.class);
        loadId(resultComponent, element);
        this.createSubComponents(resultComponent, element);
    }

    public void loadComponent() {
        assignXmlDescriptor(resultComponent, element);
        assignFrame(resultComponent);
        loadId(resultComponent, element);
        loadEnable(resultComponent, element);
        loadVisible(resultComponent, element);
        loadIcon(resultComponent, element);
        loadCaption(resultComponent, element);
        loadDescription(resultComponent, element);
        loadStyleName(resultComponent, element);
        loadResponsive(resultComponent, element);
        loadHeight(resultComponent, element);
        loadWidth(resultComponent, element);

        String dragMode = element.attributeValue("dragMode");
        if (dragMode != null) {
            resultComponent.setDragMode(LayoutDragMode.valueOf(dragMode));
        }

        loadSubComponents();
    }

    protected void loadResponsive(CssLayout component, Element element) {
        String responsive = element.attributeValue("responsive");
        if (StringUtils.isNotEmpty(responsive)) {
            component.setResponsive(BooleanUtils.toBoolean(element.attributeValue("responsive")));
        }
    }
}
