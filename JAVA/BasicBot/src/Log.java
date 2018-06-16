import java.text.SimpleDateFormat;
import java.util.Date;

import bwapi.Game;

public class Log {

    private static Game game = MyBotModule.Broodwar;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static enum Level {

	NONE(10, "NONE"), ERROR(5, "ERROR"), WARN(4, "WARN"), INFO(3, "INFO"), DEBUG(2, "DEBUG"), TRACE(1, "TRACE"), ALL(0, "ALL"),;

	private int intValue;
	private String strValue;

	Level(int intValue, String strValue) {
	    this.intValue = intValue;
	    this.strValue = strValue;
	}

	public int getIntValue() {
	    return intValue;
	}

	public String getStrValue() {
	    return strValue;
	}
    }

    private static Level CURRENT_LEVEL = Level.NONE;

    // 로그 레벨을 설정한다.
    public static void setLogLevel(Level level) {
	Log.CURRENT_LEVEL = level;
    }

    // ERROR 로그
    public static void error(String format, Object... args) {
	if (checkLogLevel(Level.ERROR)) {
	    print(String.format(format, args), Level.ERROR);
	}
    }

    // WARN 로그
    public static void warn(String format, Object... args) {
	if (checkLogLevel(Level.WARN)) {
	    print(String.format(format, args), Level.WARN);
	}
    }

    // Info 로그
    public static void info(String format, Object... args) {
	if (checkLogLevel(Level.INFO)) {
	    print(String.format(format, args), Level.INFO);
	}
    }

    // DEBUG 로그
    public static void debug(String format, Object... args) {
	if (checkLogLevel(Level.DEBUG)) {
	    print(String.format(format, args), Level.DEBUG);
	}
    }

    // TRACE 로그
    public static void trace(String format, Object... args) {
	if (checkLogLevel(Level.TRACE)) {
	    print(String.format(format, args), Level.TRACE);
	}
    }

    private static boolean checkLogLevel(Level level) {
	return CURRENT_LEVEL.getIntValue() <= level.getIntValue();
    }

    private static void print(String msg, Level level) {
	String prefix = String.format("[%s] [%-5s] [%5d] ", sdf.format(new Date()), level.getStrValue(), game.getFrameCount());
	System.out.println(prefix + msg);
    }
}
