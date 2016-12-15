package com.povmt.les.povmtprojetopiloto.Interfaces;

import com.povmt.les.povmtprojetopiloto.Models.InvestedTimeItem;

import java.util.List;

public interface InvestedTimeListener {
    void receiverTi(int statusCode, String resp);
    void receiverTi(int statusCode, List<InvestedTimeItem> investedTimeItems, String resp);
    void receiverTi(int statusCode, boolean resp);
}
