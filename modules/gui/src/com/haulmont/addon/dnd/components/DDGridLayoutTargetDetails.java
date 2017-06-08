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
import com.haulmont.addon.dnd.components.dragevent.MouseEventDetails;
import com.haulmont.addon.dnd.components.dragevent.TargetDetailsImpl;
import com.haulmont.addon.dnd.components.enums.HorizontalDropLocation;
import com.haulmont.addon.dnd.components.enums.VerticalDropLocation;
import com.haulmont.cuba.gui.components.Component;

import java.util.Map;

public class DDGridLayoutTargetDetails extends TargetDetailsImpl {

    private Component over;
    private int row = -1;
    private int column = -1;

    public DDGridLayoutTargetDetails(Map<String, Object> rawDropData, DropTarget dropTarget) {
        super(rawDropData, dropTarget);
        if (getData(Constants.DROP_DETAIL_ROW) != null) {
            row = Integer.valueOf(getData(Constants.DROP_DETAIL_ROW).toString());
        } else {
            row = -1;
        }

        if (getData(Constants.DROP_DETAIL_COLUMN) != null) {
            column = Integer.valueOf(getData(Constants.DROP_DETAIL_COLUMN).toString());
        } else {
            column = -1;
        }

        if (row != -1 && column != -1) {
            over = (Component) getData(Constants.DROP_DETAIL_GRID_OVER_COMPONENT);
        }
    }


    public Component getOverComponent() {
        return over;
    }

    public int getOverRow() {
        return row;
    }

    public int getOverColumn() {
        return column;
    }

    public HorizontalDropLocation getHorizontalDropLocation() {
        return HorizontalDropLocation.valueOf(
                String.valueOf(getData(Constants.DROP_DETAIL_HORIZONTAL_DROP_LOCATION)));
    }

    public VerticalDropLocation getVerticalDropLocation() {
        return VerticalDropLocation.valueOf(
                String.valueOf(getData(Constants.DROP_DETAIL_VERTICAL_DROP_LOCATION)));
    }

    public boolean overEmptyCell() {
        return Boolean.valueOf(
                String.valueOf(getData(Constants.DROP_DETAIL_EMPTY_CELL)));
    }

    public MouseEventDetails getMouseEvent() {
        return MouseEventDetails.deSerialize(
                String.valueOf(getData(Constants.DROP_DETAIL_MOUSE_EVENT)));
    }
}