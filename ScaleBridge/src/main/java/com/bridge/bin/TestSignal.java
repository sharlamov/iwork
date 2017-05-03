package com.bridge.bin;

import com.bridge.sensor.AlarmSensor;
import com.bridge.sensor.ScaleTrigger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class TestSignal implements Runnable {

    private final int port;
    private Thread thread;

    public TestSignal(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        TestSignal sensor = new TestSignal(5017);
        sensor.start();
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
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

                System.out.println(Arrays.toString(b));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client stoped!");
    }
}