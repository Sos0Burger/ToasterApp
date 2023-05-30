package com.messenger.messengerapp.converter;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter {
    @SuppressLint("SimpleDateFormat")
    public static String longToLocalTime(long time){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(time));
    }
}
