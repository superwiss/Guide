import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class Util {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // List의 Item을 [item1,item2,item3] 형태로 string으로 리턴한다.
    public static <T> String listAsString(List<T> list, Function<T, String> stringify) {
	String result = "[";

	for (T item : list) {
	    result += stringify.apply(item);
	}
	result = result.substring(0, result.length() - 1);
	result += "]";

	return result;
    }

    //allianceUnitHp, enemyUnitHp, allianceUnitKilledCount, enemyUnitKilledCount
    public static void writeTrainingResultToFile(String mapName, boolean isSuccess, long score, int frameCount, long allianceUnitHp, long enemyUnitHp, int allianceUnitKilledCount,
	    int enemyUnitKilledCount) {
	File file = new File("training_result.txt");
	FileWriter writer = null;

	String header = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s\r\n", "Datetime", "Map Title", "Result", "Score", "Frame count", "Alliance total HP", "Enemy total HP",
		"Alliance killed count", "Enemy killed count");

	String message = String.format("%s, %s, %s, %d, %d, %d, %d, %d, %d\r\n", sdf.format(new Date()), mapName, isSuccess ? "Success" : "Failed", score, frameCount, allianceUnitHp,
		enemyUnitHp, allianceUnitKilledCount, enemyUnitKilledCount);

	try {
	    if (!file.exists()) {
		message = header + message;
	    }
	    writer = new FileWriter(file, true);
	    writer.write(message);
	    writer.flush();
	} catch (IOException e) {
	    Log.warn("writeTrainingResultToFile has failed: ", e.toString());
	} finally {
	    try {
		if (writer != null) {
		    writer.close();
		}
	    } catch (IOException e) {
		// do nothing
	    }
	}

    }
}
