import java.util.List;
import java.util.function.Function;

public class Util {

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
}
