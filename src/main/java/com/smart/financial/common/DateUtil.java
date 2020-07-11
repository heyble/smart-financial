package com.smart.financial.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat DATE_FORMAT_LINE = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDateTime(Date date){
        return DATE_TIME_FORMAT.format(date);
    }

    public static String formatDateLine(Date date){
        return DATE_FORMAT_LINE.format(date);
    }

    public static String formatDate(Date date){
        return DATE_FORMAT.format(date);
    }

    public static Date dateStr2DateWithException(String dateStr) throws SmartException {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new SmartException("格式不正确，请输入格式正确的日期，例如：20200101");
        }
    }

    public static Date dateTimeStr2Date(String dateTimeStr){
        try {
            return DATE_TIME_FORMAT.parse(dateTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }
}
