package fenyx.engine.world;

/**
 *
 * @author DominaN
 */
public class Time {

    public final static int DEFAULT_TIME_SPEED = 50;
    public static int timeSpeed = DEFAULT_TIME_SPEED;
    private static long lastTime = System.currentTimeMillis();
    public static long time;
    //Game time
    public static byte SEASON_SUMMER = 0, SEASON_AUTUMN = 1, SEASON_WINTER = 2, SEASON_SPRING = 3;
    public static int seconds = 0, minutes = 0, hours = 0, days = 0, months = 0, years = 0, season = 0;
    public static String timeOfDay = "00:00";

    public static void checkTime() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime > 1000 / timeSpeed) {
            time++;
            seconds++;

            lastTime = currentTime;
        }

        if (seconds > 59) {
            minutes += seconds / 60;
            seconds = 0;
        }

        if (minutes > 59) {
            hours += minutes / 60;
            minutes = 0;
        }

        if (hours > 23) {
            days += hours / 24;
            hours = 0;
        }

        if (days > 30) {
            months += days / 30;
            days = 0;
        }

        if (months > 12) {
            years += months / 12;
            months = 0;
        }

        //Season change
        season = months % 4;

        //timeOfDay = ((hours < 10) ? "0".concat(String.valueOf(hours)) : String.valueOf(hours)).concat((minutes < 10) ? ":0".concat(String.valueOf(minutes)) : ":".concat(String.valueOf(minutes))).concat((seconds < 10) ? ":0".concat(String.valueOf(seconds)) : ":".concat(String.valueOf(seconds)));
        timeOfDay = ((hours < 10) ? "0".concat(String.valueOf(hours)) : String.valueOf(hours)).concat((minutes < 10) ? ":0".concat(String.valueOf(minutes)) : ":".concat(String.valueOf(minutes)));
    }
}
