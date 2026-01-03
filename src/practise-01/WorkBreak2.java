import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkBreak2 {
    public static List<String> wordBreak(String s, List<String> wordDict) {

        Set<String> dictionary = new HashSet<>(wordDict);
        Map<Integer, List<String>> memo = new HashMap<>();
        return calculate(s, dictionary, 0, memo);

    }

    private static List<String> calculate(String s, Set<String> dictionary, int index,
            Map<Integer, List<String>> memo) {

        List<String> result = new ArrayList<>();

        if (memo.containsKey(index))
            return memo.get(index);
        if (index >= s.length()) {
            result.add("");
            return result;
        }

        for (int i = index; i < s.length(); i++) {
            String current = s.substring(index, i+1);

            if (dictionary.contains(current)) {
                List<String> suffixes = calculate(s, dictionary, i + 1, memo);
                for (int j = 0; j < suffixes.size(); j++) {
                    if (suffixes.get(j).isEmpty()) {
                        result.add(current);
                    } else {
                        result.add(current + " " + suffixes.get(j));
                    }
                }
            }

        }

        memo.put(index, result);
        return result;
    }

}
