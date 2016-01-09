package com.barearild.next.v2.reisrest;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.DateTimeZoneBuilder;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateJodaDeserializer implements JsonDeserializer<DateTime> {

    public DateJodaDeserializer() {

    }

    private static DateTime deserializer(String jsonDateString) {
        if (jsonDateString == null) {
            return null;
        }

        if (jsonDateString.contains("Date")) {
            String JSONDateToMilliseconds = "/(Date\\((.*?)(\\+.*)?\\))/";
            Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
            Matcher matcher = pattern.matcher(jsonDateString);

            String timeInMS = matcher.replaceAll("$2");
            String timeZoneString = "GMT" + matcher.replaceAll("$3");

            return new DateTime(Long.valueOf(timeInMS),
                    DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneString)));
        } else {
            Pattern pattern = Pattern.compile("(.*?)(\\+.*)?");
            Matcher matcher = pattern.matcher(jsonDateString);

            if (matcher.matches()) {
                String timeInMS = matcher.group(1);
                String timeZoneString = "GMT" + matcher.group(2);

                return new DateTime(timeInMS, DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZoneString)));
            } else {
                return null;
            }
        }

    }

    private TimeZone getTimeZoneId(String jsonDate) {
        Pattern pattern = Pattern.compile("(.*?)(\\+.*)?");
        Matcher matcher = pattern.matcher(jsonDate);

        if(matcher.matches()) {
            return TimeZone.getTimeZone("GMT" + matcher.group(2));
        }

        return null;
    }

    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String jsonDateString = json.getAsJsonPrimitive().getAsString();

        return new DateTime(jsonDateString, DateTimeZone.forTimeZone(getTimeZoneId(jsonDateString)));
    }


}
