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

package com.haulmont.addon.dnd.components.dragevent;

public class MouseEventDetails {

    private static final char DELIM = ',';
    private static final int ONDBLCLICK = 0x00002;

    private MouseButton button;
    private int clientX;
    private int clientY;
    private boolean altKey;
    private boolean ctrlKey;
    private boolean metaKey;
    private boolean shiftKey;
    private int type;
    private int relativeX = -1;
    private int relativeY = -1;

    public MouseEventDetails() {
    }

    public int getClientX() {
        return clientX;
    }
    public void setClientX(int clientX) {
        this.clientX = clientX;
    }

    public int getClientY() {
        return clientY;
    }
    public void setClientY(int clientY) {
        this.clientY = clientY;
    }

    public boolean isAltKey() {
        return altKey;
    }
    public void setAltKey(boolean altKey) {
        this.altKey = altKey;
    }

    public boolean isCtrlKey() {
        return ctrlKey;
    }
    public void setCtrlKey(boolean ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public boolean isMetaKey() {
        return metaKey;
    }
    public void setMetaKey(boolean metaKey) {
        this.metaKey = metaKey;
    }

    public boolean isShiftKey() {
        return shiftKey;
    }
    public void setShiftKey(boolean shiftKey) {
        this.shiftKey = shiftKey;
    }

    public int getRelativeX() {
        return relativeX;
    }
    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }
    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public String getButtonName() {
        return button == null ? "" : button.getName();
    }

    public void setButton(MouseButton button) {
        this.button = button;
    }
    public MouseButton getButton() {
        return button;
    }

    public boolean isDoubleClick() {
        return type == ONDBLCLICK;
    }

    @Override
    public String toString() {
        return serialize();
    }

    public String serialize() {
        return button.toString() + DELIM + clientX + DELIM + clientY + DELIM
                + altKey + DELIM + ctrlKey + DELIM + metaKey + DELIM + shiftKey
                + DELIM + type + DELIM + relativeX + DELIM + relativeY;
    }

    public static MouseEventDetails deSerialize(String serializedString) {
        MouseEventDetails instance = new MouseEventDetails();
        String[] fields = serializedString.split(",");
        instance.button = MouseButton.valueOf(fields[0]);
        instance.clientX = Integer.parseInt(fields[1]);
        instance.clientY = Integer.parseInt(fields[2]);
        instance.altKey = Boolean.parseBoolean(fields[3]);
        instance.ctrlKey = Boolean.parseBoolean(fields[4]);
        instance.metaKey = Boolean.parseBoolean(fields[5]);
        instance.shiftKey = Boolean.parseBoolean(fields[6]);
        instance.type = Integer.parseInt(fields[7]);
        instance.relativeX = Integer.parseInt(fields[8]);
        instance.relativeY = Integer.parseInt(fields[9]);
        return instance;
    }

    public enum MouseButton {
        LEFT("left"), RIGHT("right"), MIDDLE("middle");
        private String name;

        MouseButton(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}