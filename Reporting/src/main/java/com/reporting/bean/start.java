package com.reporting.bean;

import com.reporting.util.PivotTable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class start {


    public static void main(String args[]) throws Exception {

        List<Object[]> data = new ArrayList<>();
        data.add(new Object[]{"01.01.2015", "01.01.2015", "A", "x", new BigDecimal(1.0), new BigDecimal(1.0)});
        data.add(new Object[]{"01.01.2015", "01.01.2015", "A", "x", new BigDecimal(2.0), new BigDecimal(1.0)});
        data.add(new Object[]{"01.02.2015", "01.01.2015", "B", "x", new BigDecimal(3.0), new BigDecimal(1.0)});
        data.add(new Object[]{"01.03.2015", "01.01.2015", "B", "y", new BigDecimal(4.0), new BigDecimal(1.0)});


        int fkey[] = {0, 1};
        int pkey[] = {2, 3};
        int skey[] = {4, 5};

        PivotTable ps = new PivotTable(fkey, pkey, skey, data);
        ps.print();

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');

        String pattern = "#,##0.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);

        System.out.println(decimalFormat.format(12123156.7));
    }

}
