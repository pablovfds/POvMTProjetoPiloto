package com.povmt.les.povmtprojetopiloto.Interfaces;

public interface UserInfoListener {
    void receiverUser(int statusCode, String resp);
    void receiverUser(int statusCode, int mHour, int mMinute);
}
