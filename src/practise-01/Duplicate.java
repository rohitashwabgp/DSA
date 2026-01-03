import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Duplicate {
    public static String longestDupSubstring(String s) {

        int n = s.length();
        String result = "";

        for (int len = n - 1; len > 0; len--) {
            HashSet<String> seen = new HashSet<>();
            for (int i = 0; i + len <= n; i++) {
                String sub = s.substring(i, i + len);
                if (seen.contains(sub)) {
                    return sub; // first found is longest since len decreasing
                }
                seen.add(sub);
            }
        }
        return result;

    }

    Map<String, Integer> unsortedMap = new HashMap<>();
// ... populate the map with data

// Sort by values in ascending order
Map<String, Integer> sortedMap = unsortedMap.entrySet()
    .stream()
    .sorted( (a,b) -> b.getValue().compareTo(a.getValue())) // This is a cleaner comparator
    .collect(Collectors.toMap(entry->
        entry.getKey(),
        entry-> entry.getValue(),
        (oldValue, newValue) -> oldValue, // Merge function in case of key collisions
        ()-> new LinkedHashMap<>() // Important: Use a LinkedHashMap to preserve order!
    ));
}
