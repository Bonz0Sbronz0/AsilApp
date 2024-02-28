package it.uniba.dib.sms232413.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        @SerializedName("legs")
        public List<Leg> legs;

        public static class Leg {
            @SerializedName("steps")
            public List<Step> steps;

            public static class Step {
                @SerializedName("html_instructions")
                public String htmlInstructions;

                @SerializedName("distance")
                public Distance distance;

                @SerializedName("start_location")
                public Location startLocation;

                @SerializedName("end_location")
                public Location endLocation;

                public static class Distance {
                    @SerializedName("text")
                    public String text;
                }

                public static class Location {
                    @SerializedName("lat")
                    public double lat;

                    @SerializedName("lng")
                    public double lng;
                }
            }
        }
    }
}