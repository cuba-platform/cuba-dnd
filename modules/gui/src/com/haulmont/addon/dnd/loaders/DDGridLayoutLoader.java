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

import com.haulmont.addon.dnd.components.DDGridLayout;
import com.haulmont.addon.dnd.components.enums.LayoutDragMode;
import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import com.haulmont.cuba.gui.xml.layout.LayoutLoader;
import com.haulmont.cuba.gui.xml.layout.loaders.ContainerLoader;
import com.haulmont.cuba.gui.xml.layout.loaders.GridLayoutLoader;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.LoggerFactory;

import java.util.*;


public class DDGridLayoutLoader extends ContainerLoader<DDGridLayout> {

    protected boolean[][] spanMatrix;

    @Override
    public void createComponent() {
        resultComponent = factory.create(DDGridLayout.NAME);
        loadId(resultComponent, element);

        Element columnsElement = element.element("columns");
        if (columnsElement == null) {
            throw new GuiDevelopmentException("'grid' element must contain 'columns' element",
                    context, "Grid ID", resultComponent.getId());
        }

        Element rowsElement = element.element("rows");
        if (rowsElement == null) {
            throw new GuiDevelopmentException("'grid' element must contain 'rows' element",
                    context, "Grid ID", resultComponent.getId());
        }

        int columnCount;
        @SuppressWarnings("unchecked")
        final List<Element> columnElements = columnsElement.elements("column");
        if (columnElements.size() == 0) {
            try {
                columnCount = Integer.parseInt(columnsElement.attributeValue("count"));
            } catch (NumberFormatException e) {
                throw new GuiDevelopmentException("'grid' element must contain either a set of 'column' elements or a 'count' attribute",
                        context, "Grid ID", resultComponent.getId());
            }
            resultComponent.setColumns(columnCount);
            for (int i = 0; i < columnCount; i++) {
                resultComponent.setColumnExpandRatio(i, 1);
            }
        } else {
            String countAttr =  columnsElement.attributeValue("count");
            if (StringUtils.isNotEmpty(countAttr)) {
                throw new GuiDevelopmentException("'grid' element can't contain a set of 'column' elements and a 'count' attribute",
                        context, "Grid ID", resultComponent.getId());
            }
            columnCount = columnElements.size();
            resultComponent.setColumns(columnCount);
            int i = 0;
            for (Element columnElement : columnElements) {
                String flex = columnElement.attributeValue("flex");
                if (!StringUtils.isEmpty(flex)) {
                    resultComponent.setColumnExpandRatio(i, Float.parseFloat(flex));
                }
                i++;
            }
        }

        @SuppressWarnings("unchecked")
        List<Element> rowElements = rowsElement.elements("row");
        Set<Element> invisibleRows = new HashSet<>();

        int rowCount = 0;
        for (Element rowElement : rowElements) {
            String visible = rowElement.attributeValue("visible");
            if (!StringUtils.isEmpty(visible)) {
                Boolean value = Boolean.valueOf(visible);

                if (BooleanUtils.toBoolean(value)) {
                    rowCount++;
                } else {
                    invisibleRows.add(rowElement);
                }
            } else {
                rowCount++;
            }
        }

        resultComponent.setRows(rowCount);

        int j = 0;
        for (Element rowElement : rowElements) {
            final String flex = rowElement.attributeValue("flex");
            if (!StringUtils.isEmpty(flex)) {
                resultComponent.setRowExpandRatio(j, Float.parseFloat(flex));
            }
            j++;
        }

        spanMatrix = new boolean[columnCount][rowElements.size()];

        int row = 0;
        for (Element rowElement : rowElements) {
            if (!invisibleRows.contains(rowElement)) {
                createSubComponents(resultComponent, rowElement, row);
                row++;
            }
        }
    }

    @Override
    public void loadComponent() {
        assignFrame(resultComponent);

        loadEnable(resultComponent, element);
        loadVisible(resultComponent, element);

        loadStyleName(resultComponent, element);

        loadSpacing(resultComponent, element);
        loadMargin(resultComponent, element);

        loadWidth(resultComponent, element);
        loadHeight(resultComponent, element);

        loadAlign(resultComponent, element);

        loadIcon(resultComponent, element);
        loadCaption(resultComponent, element);
        loadDescription(resultComponent, element);

        String dragMode = element.attributeValue("dragMode");
        if (dragMode != null) {
            resultComponent.setDragMode(LayoutDragMode.valueOf(dragMode));
        }

        loadSubComponents();
    }

    protected void createSubComponents(DDGridLayout gridLayout, Element element, int row) {
        LayoutLoader loader = beanLocator.getPrototype(LayoutLoader.NAME, context);

        int col = 0;

        for (Element subElement : element.elements()) {
            ComponentLoader componentLoader = loader.createComponent(subElement);
            pendingLoadComponents.add(componentLoader);

            Component subComponent = componentLoader.getResultComponent();

            String colspan = subElement.attributeValue("colspan");
            String rowspan = subElement.attributeValue("rowspan");

            if (col >= spanMatrix.length) {
                Map<String, Object> params = new HashMap<>();
                params.put("Grid ID", gridLayout.getId());
                String rowId = element.attributeValue("id");
                if (StringUtils.isNotEmpty(rowId)) {
                    params.put("Row ID", rowId);
                } else {
                    params.put("Row Index", row);
                }
                throw new GuiDevelopmentException("Grid column count is less than number of components in grid row", context, params);
            }
            while (spanMatrix[col][row]) {
                col++;
            }

            if (StringUtils.isEmpty(colspan) && StringUtils.isEmpty(rowspan)) {
                addSubComponent(gridLayout, subComponent, col, row, col, row);
            } else {
                int cspan = 1;
                int rspan = 1;

                if (StringUtils.isNotEmpty(colspan)) {
                    cspan = Integer.parseInt(colspan);
                    if (cspan < 1) {
                        throw new GuiDevelopmentException("GridLayout colspan can not be less than 1",
                                context, "colspan", cspan);
                    }
                    if (cspan == 1) {
                        LoggerFactory.getLogger(GridLayoutLoader.class).warn("Do not use colspan=\"1\", it will have no effect");
                    }
                }

                if (StringUtils.isNotEmpty(rowspan)) {
                    rspan = Integer.parseInt(rowspan);
                    if (rspan < 1) {
                        throw new GuiDevelopmentException("GridLayout rowspan can not be less than 1", context, "rowspan", rspan);
                    }
                    if (rspan == 1) {
                        LoggerFactory.getLogger(GridLayoutLoader.class).warn("Do not use rowspan=\"1\", it will have no effect");
                    }
                }

                fillSpanMatrix(col, row, cspan, rspan);

                int endColumn = col + cspan - 1;
                int endRow = row + rspan - 1;

                addSubComponent(gridLayout, subComponent, col, row, endColumn, endRow);
            }

            col++;
        }
    }

    protected void addSubComponent(DDGridLayout grid, Component subComponent, int c1, int r1, int c2, int r2) {
        grid.add(subComponent, c1, r1, c2, r2);
    }

    protected void fillSpanMatrix(int col, int row, int cspan, int rspan) {
        for (int i = col; i < (col + cspan); i++) {
            for (int j = row; j < (row + rspan); j++) {
                if (spanMatrix[i][j]) {
                    throw new GuiDevelopmentException("Grid layout prohibits component overlapping", context);
                }
                spanMatrix[i][j] = true;
            }
        }
    }
}