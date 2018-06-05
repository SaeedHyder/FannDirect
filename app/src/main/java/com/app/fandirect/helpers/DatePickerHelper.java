package com.app.fandirect.helpers;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.Window;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created on 4/29/2017.
 */

public class DatePickerHelper {
    private DatePickerDialog dialog;

    public DatePickerHelper() {

    }
    public DatePickerDialog initDateDialog(Context context, int year, int month, int day, DatePickerDialog.OnDateSetListener dateSetListener,String tag){
        this.dialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        Calendar min = Calendar.getInstance();
        min.set(year,month,day);
        this.dialog.getDatePicker().setMinDate(min.getTimeInMillis());
        Calendar max = Calendar.getInstance();
        max.set(year,month,day+30);
        this.dialog.getDatePicker().setMaxDate(max.getTimeInMillis());
        return dialog;
    }
    public DatePickerDialog initDateDialog(Context context, int year, int month, int day, DatePickerDialog.OnDateSetListener dateSetListener, String preferredDate, Date date){
        this.dialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        return dialog;
    }
    public DatePickerDialog initDateDialog(Context context, int year, int month, int day, DatePickerDialog.OnDateSetListener dateSetListener, String tag, int minusdays){
        this.dialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        Calendar max = Calendar.getInstance();
        max.set(year,month,day-minusdays);
        this.dialog.getDatePicker().setMaxDate(max.getTimeInMillis());
         this.dialog.setTitle("");
       // this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    public void showDate() {
        if (this.dialog == null){
            throw  new NullPointerException("Initialize Dialog First");
        }else {
            this.dialog.show();
        }


    }

    public String getShortDayName(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        return new SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.getTime());
    }
    public String getFullDayName(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
    }
    public String getShortMonthName(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        return new SimpleDateFormat("MMM", Locale.ENGLISH).format(calendar.getTime());
    }
    public String getFullMonthName(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        return new SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.getTime());
    }
    public String getStringDate(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(year, month, day);
        return new SimpleDateFormat("yyyy MMM dd", Locale.ENGLISH).format(calendar.getTime());
    }

}
