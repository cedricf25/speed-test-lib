# JSpeedTest library ProGuard rules

-keep class fr.bmartel.speedtest.** { *; }
-keep interface fr.bmartel.speedtest.** { *; }

-keepclassmembers class fr.bmartel.speedtest.SpeedTestReport {
    <fields>;
    <methods>;
}

-keepclassmembers enum fr.bmartel.speedtest.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Apache Commons Net
-keep class org.apache.commons.net.** { *; }
-dontwarn org.apache.commons.net.**
