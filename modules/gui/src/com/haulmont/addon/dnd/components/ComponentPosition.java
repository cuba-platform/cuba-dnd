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

import com.haulmont.addon.dnd.components.enums.Unit;

import java.io.Serializable;

public class ComponentPosition implements Serializable {

    private int zIndex = -1;
    private Float topValue = null;
    private Float rightValue = null;
    private Float bottomValue = null;
    private Float leftValue = null;

    private Unit topUnits = Unit.PIXELS;
    private Unit rightUnits = Unit.PIXELS;
    private Unit bottomUnits = Unit.PIXELS;
    private Unit leftUnits = Unit.PIXELS;

    public void setCSSString(String css) {
        topValue = rightValue = bottomValue = leftValue = null;
        topUnits = rightUnits = bottomUnits = leftUnits = Unit.PIXELS;
        zIndex = -1;
        if (css == null) {
            return;
        }

        String[] cssProperties = css.split(";");
        for (int i = 0; i < cssProperties.length; i++) {
            String[] keyValuePair = cssProperties[i].split(":");
            String key = keyValuePair[0].trim();
            if (key.equals("")) {
                continue;
            }
            if (key.equals("z-index")) {
                zIndex = Integer.parseInt(keyValuePair[1].trim());
            } else {
                String value;
                if (keyValuePair.length > 1) {
                    value = keyValuePair[1].trim();
                } else {
                    value = "";
                }
                String symbol = value.replaceAll("[0-9\\.\\-]+", "");
                if (!symbol.equals("")) {
                    value = value.substring(0, value.indexOf(symbol))
                            .trim();
                }
                float v = Float.parseFloat(value);
                Unit unit = Unit.getUnitFromSymbol(symbol);
                if (key.equals("top")) {
                    topValue = v;
                    topUnits = unit;
                } else if (key.equals("right")) {
                    rightValue = v;
                    rightUnits = unit;
                } else if (key.equals("bottom")) {
                    bottomValue = v;
                    bottomUnits = unit;
                } else if (key.equals("left")) {
                    leftValue = v;
                    leftUnits = unit;
                }
            }
        }
    }

    public String getCSSString() {
        String s = "";
        if (topValue != null) {
            s += "top:" + topValue + topUnits.getSymbol() + ";";
        }
        if (rightValue != null) {
            s += "right:" + rightValue + rightUnits.getSymbol() + ";";
        }
        if (bottomValue != null) {
            s += "bottom:" + bottomValue + bottomUnits.getSymbol() + ";";
        }
        if (leftValue != null) {
            s += "left:" + leftValue + leftUnits.getSymbol() + ";";
        }
        if (zIndex >= 0) {
            s += "z-index:" + zIndex + ";";
        }
        return s;
    }

    public void setTop(Float topValue, Unit topUnits) {
        this.topValue = topValue;
        this.topUnits = topUnits;
    }

    public void setRight(Float rightValue, Unit rightUnits) {
        this.rightValue = rightValue;
        this.rightUnits = rightUnits;
    }

    public void setBottom(Float bottomValue, Unit bottomUnits) {
        this.bottomValue = bottomValue;
        this.bottomUnits = bottomUnits;
    }

    public void setLeft(Float leftValue, Unit leftUnits) {
        this.leftValue = leftValue;
        this.leftUnits = leftUnits;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public void setTopValue(Float topValue) {
        this.topValue = topValue;
    }

    public Float getTopValue() {
        return topValue;
    }

    public Float getRightValue() {
        return rightValue;
    }

    public void setRightValue(Float rightValue) {
        this.rightValue = rightValue;
    }

    public Float getBottomValue() {
        return bottomValue;
    }

    public void setBottomValue(Float bottomValue) {
        this.bottomValue = bottomValue;
    }

    public Float getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(Float leftValue) {
        this.leftValue = leftValue;
    }

    public Unit getTopUnits() {
        return topUnits;
    }

    public void setTopUnits(Unit topUnits) {
        this.topUnits = topUnits;
    }

    public Unit getRightUnits() {
        return rightUnits;
    }

    public void setRightUnits(Unit rightUnits) {
        this.rightUnits = rightUnits;
    }

    public Unit getBottomUnits() {
        return bottomUnits;
    }

    public void setBottomUnits(Unit bottomUnits) {
        this.bottomUnits = bottomUnits;
    }

    public Unit getLeftUnits() {
        return leftUnits;
    }

    public void setLeftUnits(Unit leftUnits) {
        this.leftUnits = leftUnits;
    }

    public int getZIndex() {
        return zIndex;
    }

    @Override
    public String toString() {
        return getCSSString();
    }
}