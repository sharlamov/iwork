package md.sh.bin;

import md.sh.model.CustomItem;
import md.sh.model.DataSet;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class App {
    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public static void main(String[] args) throws Exception {

        ReportGear ped = new ReportGear();

        CustomItem ci1 = new CustomItem(1, "CustomItem1");
        CustomItem ci2 = new CustomItem(2, "CustomItem2");

        DataSet header = new DataSet("test", 5, "tdate", new Date(), "ttext", "4791038672272", "tnumber", -10398756.654);
        DataSet master = new DataSet("ord", 0, "test", 15.99, "test1", ci1, "test2", 168.88, "test3", 0);

        Random r = new Random();

        for (int i = 0; i < 5; i++) {
            master.add(Arrays.asList(i + 1, r.nextInt(1000), r.nextInt(2) == 0 ? ci1 : ci2, r.nextInt(1000), r.nextInt(2)));
        }

        ped.make(new File("incomex.xls"), header, master);


        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        //runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: "
                + bytesToMegabytes(memory));
    }
}


