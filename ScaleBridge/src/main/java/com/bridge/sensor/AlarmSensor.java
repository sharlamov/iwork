package com.bridge.sensor;

import com.bridge.bin.Util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class AlarmSensor extends ArrayList<ScaleTrigger> implements Runnable {

    private final static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final int port;
    private Thread thread;

    public AlarmSensor(int port) {
        this.port = port;
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[12];
            System.out.println("Client started!");
            while (true) {
                Thread.yield();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] b = packet.getData();

                String signal = byteHexToAscii(b);
                //System.out.println(signal);
                for (ScaleTrigger trg : this) {
                    if (trg.check(signal)) {
                        //Util.log("signal checked true");
                        trg.sendSignal();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client stoped!");
    }

    private String byteHexToAscii(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            char[] b = {hexArray[v >>> 4], hexArray[v & 0x0F]};
            sb.append((char) Integer.parseInt(new String(b), 16));
        }
        return sb.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
