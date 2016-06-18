package com.bin;

import java.util.Scanner;

public class ConsoleReadingDemo {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        while(true){
            System.out.print("Please enter user name : ");
            String username = in.nextLine();
            if(username.contains("*"))
                break;
            System.out.println("You entered : " + username);
        }



    }
}