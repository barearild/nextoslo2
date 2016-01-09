package com.barearild.next.v2.reisrest;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TransporttypeDeserializer implements JsonDeserializer<Transporttype> {

    public Transporttype deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        int transportTypeJsonValueAsInt;

        try {
            transportTypeJsonValueAsInt = json.getAsJsonPrimitive().getAsInt();
        } catch (NumberFormatException nfe) {
            transportTypeJsonValueAsInt = Transporttype.Dummy.ordinal();
        }
        Transporttype transportType = Transporttype.Dummy;
        if (transportTypeJsonValueAsInt >= 0 && transportTypeJsonValueAsInt < 9) {
            transportType = Transporttype.values()[transportTypeJsonValueAsInt];
        }

        return transportType;

    }
}
