package com.povmt.les.povmtprojetopiloto.Models.ChartAxisFormatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by emanoel on 11/12/16.
 */

public class MyXAxisValueFormatter implements IAxisValueFormatter {

    private String[] values;

    public MyXAxisValueFormatter(String[] values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int) value];
    }
}
