/*
 * Author: Sami Salkosuo, sami.salkosuo@fi.ibm.com
 *
 * (c) Copyright IBM Corp. 2007
 */
package com.barearild.next.v2.location.libs;

import android.location.Location;

public class CoordinateConversion {

    public CoordinateConversion() {

    }

    public static double[] utm2LatLon(int x, int y) {
        return new CoordinateConversion().utm2LatLon("32 N " + x + " " + y);
    }

    double[] utm2LatLon(String UTM) {
        UTM2LatLon c = new UTM2LatLon();
        return c.convertUTMToLatLong(UTM);
    }

    public UtmLocation latLon2UTM(double latitude, double longitude) {
        LatLon2UTM c = new LatLon2UTM();
        return c.convertLatLonToUTM(latitude, longitude);

    }

    private void validate(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0
                || longitude >= 180.0) {
            throw new IllegalArgumentException(
                    "Legal ranges: latitude [-90,90], longitude [-180,180).");
        }

    }

    double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    private double POW(double a, double b) {
        return Math.pow(a, b);
    }

    private double SIN(double value) {
        return Math.sin(value);
    }

    private double COS(double value) {
        return Math.cos(value);
    }

    private double TAN(double value) {
        return Math.tan(value);
    }

    public UtmLocation location2Utm(Location location) {
        return latLon2UTM(location.getLatitude(), location.getLongitude());
    }

    private class LatLon2UTM {
        // equatorial radius
        final double equatorialRadius = 6378137;
        // polar radius
        final double polarRadius = 6356752.314;
        // eccentricity
        final double e = Math.sqrt(1 - POW(polarRadius / equatorialRadius, 2));
        final double e1sq = e * e / (1 - e * e);
        // scale factor
        final double k0 = 0.9996;

        // Lat Lon to UTM variables
        final double A0 = 6367449.146;
        final double B0 = 16038.42955;
        final double C0 = 16.83261333;
        final double D0 = 0.021984404;
        final double E0 = 0.000312705;
        final double sin1 = 4.84814E-06;
        // r curv 1
        double rho = 6368573.744;
        // r curv 2
        double nu = 6389236.914;
        // Calculate Meridional Arc Length
        // Meridional Arc
        double S = 5103266.421;
        // Calculation Constants
        // Delta Long
        double p = -0.483084;
        // Coefficients for UTM Coordinates
        double K1 = 5101225.115;
        double K2 = 3750.291596;
        double K3 = 1.397608151;
        double K4 = 214839.3105;
        double K5 = -2.995382942;
        double A6 = -1.00541E-07;

        public UtmLocation convertLatLonToUTM(double latitude, double longitude) {
            UtmLocation utmLocation = new UtmLocation();
            validate(latitude, longitude);

            setVariables(latitude, longitude);

            utmLocation.setLongZone(getLongZone(longitude));
            LatZones latZones = new LatZones();
            utmLocation.setLatZone(latZones.getLatZone(latitude));

            utmLocation.setEasting(getEasting());
            utmLocation.setNorthing(getNorthing(latitude));

            return utmLocation;

        }

        void setVariables(double latitude, double longitude) {
            latitude = degreeToRadian(latitude);
            rho = equatorialRadius * (1 - e * e)
                    / POW(1 - POW(e * SIN(latitude), 2), 3 / 2.0);

            nu = equatorialRadius / POW(1 - POW(e * SIN(latitude), 2), (1 / 2.0));

            double var1;
            if (longitude < 0.0) {
                var1 = ((int) ((180 + longitude) / 6.0)) + 1;
            } else {
                var1 = ((int) (longitude / 6)) + 31;
            }
            double var2 = (6 * var1) - 183;
            double var3 = longitude - var2;
            p = var3 * 3600 / 10000;

            S = A0 * latitude - B0 * SIN(2 * latitude) + C0 * SIN(4 * latitude) - D0
                    * SIN(6 * latitude) + E0 * SIN(8 * latitude);

            K1 = S * k0;
            K2 = nu * SIN(latitude) * COS(latitude) * POW(sin1, 2) * k0 * (100000000)
                    / 2;
            K3 = ((POW(sin1, 4) * nu * SIN(latitude) * Math.pow(COS(latitude), 3)) / 24)
                    * (5 - POW(TAN(latitude), 2) + 9 * e1sq * POW(COS(latitude), 2) + 4
                    * POW(e1sq, 2) * POW(COS(latitude), 4))
                    * k0
                    * (10000000000000000L);

            K4 = nu * COS(latitude) * sin1 * k0 * 10000;

            K5 = POW(sin1 * COS(latitude), 3) * (nu / 6)
                    * (1 - POW(TAN(latitude), 2) + e1sq * POW(COS(latitude), 2)) * k0
                    * 1000000000000L;

            A6 = (POW(p * sin1, 6) * nu * SIN(latitude) * POW(COS(latitude), 5) / 720)
                    * (61 - 58 * POW(TAN(latitude), 2) + POW(TAN(latitude), 4) + 270
                    * e1sq * POW(COS(latitude), 2) - 330 * e1sq
                    * POW(SIN(latitude), 2)) * k0 * (1E+24);

        }

        String getLongZone(double longitude) {
            double longZone;
            if (longitude < 0.0) {
                longZone = ((180.0 + longitude) / 6) + 1;
            } else {
                longZone = (longitude / 6) + 31;
            }
            String val = String.valueOf((int) longZone);
            if (val.length() == 1) {
                val = "0" + val;
            }
            return val;
        }

        double getNorthing(double latitude) {
            double northing = K1 + K2 * p * p + K3 * POW(p, 4);
            if (latitude < 0.0) {
                northing = 10000000 + northing;
            }
            return northing;
        }

        double getEasting() {
            return 500000 + (K4 * p + K5 * POW(p, 3));
        }

    }

    private class UTM2LatLon {
        final String southernHemisphere = "ACDEFGHJKLM";
        final double a = 6378137;
        final double e = 0.081819191;
        final double e1sq = 0.006739497;
        final double k0 = 0.9996;
        double easting;
        double northing;
        int zone;
        double arc;
        double mu;
        double ei;
        double ca;
        double cb;
        double cc;
        double cd;
        double n0;
        double r0;
        double _a1;
        double dd0;
        double t0;
        double Q0;
        double lof1;
        double lof2;
        double lof3;
        double _a2;
        double phi1;
        double fact1;
        double fact2;
        double fact3;
        double fact4;
        double zoneCM;
        double _a3;

        String getHemisphere(String latZone) {
            String hemisphere = "N";
            if (southernHemisphere.contains(latZone)) {
                hemisphere = "S";
            }
            return hemisphere;
        }

        public double[] convertUTMToLatLong(String UTM) {
            double[] latlon = {0.0, 0.0};
            String[] utm = UTM.split(" ");
            zone = Integer.parseInt(utm[0]);
            String latZone = utm[1];
            easting = Double.parseDouble(utm[2]);
            northing = Double.parseDouble(utm[3]);
            String hemisphere = getHemisphere(latZone);
            double latitude;
            double longitude;

            if (hemisphere.equals("S")) {
                northing = 10000000 - northing;
            }
            setVariables();
            latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

            if (zone > 0) {
                zoneCM = 6 * zone - 183.0;
            } else {
                zoneCM = 3.0;

            }

            longitude = zoneCM - _a3;
            if (hemisphere.equals("S")) {
                latitude = -latitude;
            }

            latlon[0] = latitude;
            latlon[1] = longitude;
            return latlon;

        }

        void setVariables() {
            arc = northing / k0;
            mu = arc
                    / (a * (1 - POW(e, 2) / 4.0 - 3 * POW(e, 4) / 64.0 - 5 * POW(e, 6) / 256.0));

            ei = (1 - POW((1 - e * e), (1 / 2.0)))
                    / (1 + POW((1 - e * e), (1 / 2.0)));

            ca = 3 * ei / 2 - 27 * POW(ei, 3) / 32.0;

            cb = 21 * POW(ei, 2) / 16 - 55 * POW(ei, 4) / 32;
            cc = 151 * POW(ei, 3) / 96;
            cd = 1097 * POW(ei, 4) / 512;
            phi1 = mu + ca * SIN(2 * mu) + cb * SIN(4 * mu) + cc * SIN(6 * mu) + cd
                    * SIN(8 * mu);

            n0 = a / POW((1 - POW((e * SIN(phi1)), 2)), (1 / 2.0));

            r0 = a * (1 - e * e) / POW((1 - POW((e * SIN(phi1)), 2)), (3 / 2.0));
            fact1 = n0 * TAN(phi1) / r0;

            _a1 = 500000 - easting;
            dd0 = _a1 / (n0 * k0);
            fact2 = dd0 * dd0 / 2;

            t0 = POW(TAN(phi1), 2);
            Q0 = e1sq * POW(COS(phi1), 2);
            fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * POW(dd0, 4)
                    / 24;

            fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * t0 * t0 - 252 * e1sq - 3 * Q0
                    * Q0)
                    * POW(dd0, 6) / 720;

            //
            lof1 = _a1 / (n0 * k0);
            lof2 = (1 + 2 * t0 + Q0) * POW(dd0, 3) / 6.0;
            lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * POW(Q0, 2) + 8 * e1sq + 24 * POW(t0, 2))
                    * POW(dd0, 5) / 120;
            _a2 = (lof1 - lof2 + lof3) / COS(phi1);
            _a3 = _a2 * 180 / Math.PI;

        }

    }

    private class LatZones {
        private final char[] letters = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Z'};

        private final int[] degrees = {-90, -84, -72, -64, -56, -48, -40, -32, -24, -16,
                -8, 0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84};

        private final char[] negLetters = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                'L', 'M'};

        private final int[] negDegrees = {-90, -84, -72, -64, -56, -48, -40, -32, -24,
                -16, -8};

        private final char[] posLetters = {'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Z'};

        private final int[] posDegrees = {0, 8, 16, 24, 32, 40, 48, 56, 64, 72, 84};

        private final int arrayLength = 22;

        public LatZones() {
        }

        public int getLatZoneDegree(String letter) {
            char ltr = letter.charAt(0);
            for (int i = 0; i < arrayLength; i++) {
                if (letters[i] == ltr) {
                    return degrees[i];
                }
            }
            return -100;
        }

        public String getLatZone(double latitude) {
            int latIndex = -2;
            int lat = (int) latitude;

            if (lat >= 0) {
                int len = posLetters.length;
                for (int i = 0; i < len; i++) {
                    if (lat == posDegrees[i]) {
                        latIndex = i;
                        break;
                    }

                    if (lat > posDegrees[i]) {
                        continue;
                    } else {
                        latIndex = i - 1;
                        break;
                    }
                }
            } else {
                int len = negLetters.length;
                for (int i = 0; i < len; i++) {
                    if (lat == negDegrees[i]) {
                        latIndex = i;
                        break;
                    }

                    if (lat < negDegrees[i]) {
                        latIndex = i - 1;
                        break;
                    } else {
                        continue;
                    }

                }

            }

            if (latIndex == -1) {
                latIndex = 0;
            }
            if (lat >= 0) {
                if (latIndex == -2) {
                    latIndex = posLetters.length - 1;
                }
                return String.valueOf(posLetters[latIndex]);
            } else {
                if (latIndex == -2) {
                    latIndex = negLetters.length - 1;
                }
                return String.valueOf(negLetters[latIndex]);

            }
        }

    }

}
