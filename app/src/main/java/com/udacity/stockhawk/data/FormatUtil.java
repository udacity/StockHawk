package com.udacity.stockhawk.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {

    public static DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    public static DecimalFormat percentageFormat = getPercentageFormat();

    private static DecimalFormat getPercentageFormat() {
        DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setPositivePrefix("+");
        return format;
    }
}
