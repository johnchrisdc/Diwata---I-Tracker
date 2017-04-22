package xyz.jcdc.diwata.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by jcdc on 4/22/17.
 */

public class NumberHelper {
    public static String toDecimalPlaces(double v) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(v);
    }
}
