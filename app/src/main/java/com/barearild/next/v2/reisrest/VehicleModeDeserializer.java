package com.barearild.next.v2.reisrest;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class VehicleModeDeserializer implements JsonDeserializer<VehicleMode> {

    public VehicleMode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        int vehicleModeIndex;

        try {
            vehicleModeIndex = json.getAsJsonPrimitive().getAsInt();
        } catch (NumberFormatException nfe) {
            vehicleModeIndex = VehicleMode.Metro.ordinal();
        }

        VehicleMode vehicleMode = VehicleMode.Metro;
        if (vehicleModeIndex >= 0 && vehicleModeIndex < 9) {
            vehicleMode = VehicleMode.values()[vehicleModeIndex];
        }


        return vehicleMode;

    }
}
