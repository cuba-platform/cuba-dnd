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

package com.haulmont.addon.dnd.components;

import com.haulmont.addon.dnd.components.dragevent.Constants;
import com.haulmont.addon.dnd.components.dragevent.DropTarget;
import com.haulmont.addon.dnd.components.dragevent.TargetDetailsImpl;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.addon.dnd.components.enums.HorizontalDropLocation;

import java.util.Map;

public class DragAndDropWrapperTargetDetails extends TargetDetailsImpl {

    public DragAndDropWrapperTargetDetails(Map<String, Object> rawDropData, DropTarget dropTarget) {
        super(rawDropData, dropTarget);
    }

    public Integer getAbsoluteLeft() {
        return (Integer) getData(Constants.DROP_DETAIL_ABSOLUTE_LEFT);
    }


    public Integer getAbsoluteTop() {
        return (Integer) getData(Constants.DROP_DETAIL_ABSOLUTE_TOP);
    }


    public VerticalDropLocation getVerticalDropLocation() {
        return VerticalDropLocation.valueOf(
                String.valueOf(getData(Constants.DROP_DETAIL_VERTICAL_DROP_LOCATION)));
    }

    public HorizontalDropLocation getHorizontalDropLocation() {
        return HorizontalDropLocation.valueOf(
                String.valueOf(getData(Constants.DROP_DETAIL_HORIZONTAL_DROP_LOCATION)));
    }
}