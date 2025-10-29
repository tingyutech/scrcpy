package com.genymobile.scrcpy.control;

import com.genymobile.scrcpy.device.Device;

import java.util.List;

public final class DeviceMessage {

    public static final int TYPE_CLIPBOARD = 0;
    public static final int TYPE_ACK_CLIPBOARD = 1;
    public static final int TYPE_UHID_OUTPUT = 2;
    public static final int TYPE_GET_APP_LIST_PAYLOAD = 3;
    public static final int TYPE_DISPLAY_SIZE_CHANGED = 4;

    private int type;
    private String text;
    private long sequence;
    private int id;
    private byte[] data;
    private List<Device.AppInfo> apps;
    private int displayId;
    private int width;
    private int height;

    private DeviceMessage() {
    }

    public static DeviceMessage createClipboard(String text) {
        DeviceMessage event = new DeviceMessage();
        event.type = TYPE_CLIPBOARD;
        event.text = text;
        return event;
    }

    public static DeviceMessage createAckClipboard(long sequence) {
        DeviceMessage event = new DeviceMessage();
        event.type = TYPE_ACK_CLIPBOARD;
        event.sequence = sequence;
        return event;
    }

    public static DeviceMessage createUhidOutput(int id, byte[] data) {
        DeviceMessage event = new DeviceMessage();
        event.type = TYPE_UHID_OUTPUT;
        event.id = id;
        event.data = data;
        return event;
    }

    public static DeviceMessage createGetAppListPayload(int id, List<Device.AppInfo> apps) {
        DeviceMessage event = new DeviceMessage();
        event.type = TYPE_GET_APP_LIST_PAYLOAD;
        event.id = id;
        event.apps = apps;
        return event;
    }

    public static DeviceMessage createDisplaySizeChanged(int displayId, int width, int height) {
        DeviceMessage event = new DeviceMessage();
        event.type = TYPE_DISPLAY_SIZE_CHANGED;
        event.displayId = displayId;
        event.width = width;
        event.height = height;
        return event;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public long getSequence() {
        return sequence;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public List<Device.AppInfo> getApps() {
        return apps;
    }

    public int getDisplayId() {
        return displayId;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
