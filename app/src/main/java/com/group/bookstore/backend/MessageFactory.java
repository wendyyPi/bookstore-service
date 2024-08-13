package com.group.bookstore.backend;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * This class is to create formatted message using factory pattern
 * @author Fangzhou Liu
 */
public class MessageFactory {
    public Message create(User receiver, User addresser, String content) {
        if (receiver.getBlackList().contains(addresser.getName()))
            return null;
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        LocalTime now = LocalTime.now();
        formattedDate += " ";
        formattedDate += now.format(DateTimeFormatter.ofPattern("HH:mm"));

        return new Message(receiver.getName(), addresser.getName(), content, formattedDate);
    }
}
