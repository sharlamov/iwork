package com.reporting.bean;

import java.math.BigDecimal;

public class start {


    public static void main(String args[]) throws Exception {

        String right = "5466442680550699541780550698435";
        String pass = "111";

        System.out.println(right);
        System.out.println(encode(pass));
    }

    public static BigDecimal defVal(BigDecimal v_result){
        String text = v_result.toString();
        int v_length = text.length();
        BigDecimal v_defval = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            v_defval = v_defval.add(new BigDecimal((int)(text.charAt(i))));
        }
        return v_defval;
    }

    public static String encode(String pass){
        BigDecimal TEN = new BigDecimal(10);
        BigDecimal v_result = new BigDecimal(0);
        for (int i = 0; i < 30; i++) {
            BigDecimal ascii = pass.length() > i ? new BigDecimal((int) pass.charAt(i)) : new BigDecimal((i+1)*7).add(defVal(v_result));
            v_result = v_result.multiply(TEN).add(ascii);
        }
        return v_result.toString();
    }

}
