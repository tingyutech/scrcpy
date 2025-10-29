package com.genymobile.scrcpy.control;

import com.genymobile.scrcpy.device.Device;
import com.genymobile.scrcpy.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DeviceMessageWriter {

    private static final int MESSAGE_MAX_SIZE = 1 << 18; // 256k
    public static final int CLIPBOARD_TEXT_MAX_LENGTH = MESSAGE_MAX_SIZE - 5; // type: 1 byte; length: 4 bytes
    public static final int APPNAME_TEXT_MAX_LENGTH = CLIPBOARD_TEXT_MAX_LENGTH; // type: 1 byte; length: 4 bytes

    private final DataOutputStream dos;

    public DeviceMessageWriter(OutputStream rawOutputStream) {
        dos = new DataOutputStream(new BufferedOutputStream(rawOutputStream));
    }

    public void write(DeviceMessage msg) throws IOException {
        int type = msg.getType();
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream tempDos = new DataOutputStream(buffer);
        
        switch (type) {
            case DeviceMessage.TYPE_CLIPBOARD:
                String text = msg.getText();
                byte[] raw = text.getBytes(StandardCharsets.UTF_8);
                int len = StringUtils.getUtf8TruncationIndex(raw, CLIPBOARD_TEXT_MAX_LENGTH);
                tempDos.writeInt(len);
                tempDos.write(raw, 0, len);
                break;
            case DeviceMessage.TYPE_ACK_CLIPBOARD:
                tempDos.writeLong(msg.getSequence());
                break;
            case DeviceMessage.TYPE_UHID_OUTPUT:
                tempDos.writeShort(msg.getId());
                byte[] data = msg.getData();
                tempDos.writeShort(data.length);
                tempDos.write(data);
                break;
            case DeviceMessage.TYPE_GET_APP_LIST_PAYLOAD:
                List<Device.AppInfo> apps = msg.getApps();

                tempDos.writeInt(msg.getId());
                tempDos.writeInt(apps.size());

                for (int i = 0; i < apps.size(); i ++) {
                    Device.AppInfo info = apps.get(i);

                    tempDos.writeByte(info.isVisible ? 1 : 0);

                    byte[] rawAppName = info.appName.getBytes(StandardCharsets.UTF_8);
                    tempDos.writeInt(rawAppName.length);
                    tempDos.write(rawAppName);

                    byte[] rawPackageName = info.packageName.getBytes(StandardCharsets.UTF_8);
                    tempDos.writeInt(rawPackageName.length);
                    tempDos.write(rawPackageName);
                }

                break;
            case DeviceMessage.TYPE_DISPLAY_SIZE_CHANGED:
                tempDos.writeInt(msg.getDisplayId());
                tempDos.writeInt(msg.getWidth());
                tempDos.writeInt(msg.getHeight());
                break;
            default:
                throw new ControlProtocolException("Unknown event type: " + type);
        }
        
        byte[] content = buffer.toByteArray();
        
        dos.writeInt(content.length);
        dos.writeByte(type);
        dos.write(content);
        dos.flush();
    }
}
