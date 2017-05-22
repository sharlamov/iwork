package com.orders.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public class WebUtil {

    private static BigDecimal defVal(BigDecimal v_result) {
        String text = v_result.toString();
        int v_length = text.length();
        BigDecimal v_defval = BigDecimal.ZERO;
        for (int i = 0; i < v_length; i++) {
            v_defval = v_defval.add(new BigDecimal((int) (text.charAt(i))));
        }
        return v_defval;
    }

    public static String encode(String pass) {
        BigDecimal TEN = BigDecimal.TEN;
        BigDecimal v_result = BigDecimal.ZERO;
        for (int i = 0; i < 30; i++) {
            BigDecimal ascii = pass.length() > i ? new BigDecimal((int) pass.charAt(i)) : new BigDecimal((i + 1) * 7).add(defVal(v_result));
            v_result = v_result.multiply(TEN).add(ascii);
        }
        return v_result.toString();
    }

    public static String getHost() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return req.getRemoteHost();
    }

    public static String format(String text, Object param) {
        return text.replaceAll("%s", param.toString());
    }
}
