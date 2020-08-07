package com.thoughtworks.todo_list.data.datasource;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverters {
    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }

    @TypeConverter
    public Date fromTimestamp(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
