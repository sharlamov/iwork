package md.bin;

import javax.imageio.ImageIO;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class Image_client {

    public static void main(String[] args) {

        try {

            //URL url = new URL("http://192.168.0.57/image");
            URL url = new URL("file:/D:/tmp/IMG_1970.JPG");
            long start3 = System.currentTimeMillis();
            ImageIO.read(new BufferedInputStream(url.openStream()));
            long end3 = System.currentTimeMillis();
            System.out.println("time2=" + (end3 - start3));

            long start = System.currentTimeMillis();
            ImageIO.read(new BufferedInputStream(url.openStream()));
            long end = System.currentTimeMillis();
            System.out.println("time0=" + (end - start));


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}