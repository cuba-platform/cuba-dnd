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

import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractBoxLoader;
import com.haulmont.addon.dnd.components.DDHorizontalLayout;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;


public class DDHorizontalLayoutLoader extends AbstractBoxLoader {

    @Override
    public void createComponent() {
        resultComponent = factory.createComponent(DDHorizontalLayout.class);
        createSubComponents((Component.Container) resultComponent, element);
    }

    @Override
    public void loadComponent() {
        String dragMode = element.attributeValue("dragMode");
        if (dragMode != null) {
            ((DDHorizontalLayout) resultComponent).setDragMode(LayoutDragMode.valueOf(dragMode));
        }
        super.loadComponent();
    }
}
