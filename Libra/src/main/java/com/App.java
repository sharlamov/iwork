package com;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        //writeTestToPort("COM1");
        //printPortNames();
        readData("COM1");
    }

    public static void writeTestToPort(String port) {
        SerialPort serialPort = new SerialPort(port);
        try {
            System.out.println("Port opened: " + serialPort.openPort());
            System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0));
            System.out.println("\"Hello World!!!\" successfully writen to port: " + serialPort.writeBytes("Hello World!!!".getBytes()));
            System.out.println("Port closed: " + serialPort.closePort());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    public static void printPortNames() {
        String[] portNames = SerialPortList.getPortNames();
        System.out.print(Arrays.toString(portNames));
    }

    public static void readData(String port){
        SerialPort serialPort = new SerialPort(port);
        try {
            System.out.println(port);
            serialPort.openPort();//Open serial port
            serialPort.setParams(9600, 8, 1, 0);//Set params.
            byte[] buffer = serialPort.readBytes(10);//Read 10 bytes from serial port
            System.out.print(Arrays.toString(buffer));
            serialPort.closePort();//Close serial port
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

}
