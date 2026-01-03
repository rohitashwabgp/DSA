import java.util.*;

public class EasyShortestSubstring {

    public static String shortestSubstring(String s) {
        char[] chars = s.toCharArray();
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < chars.length; i++) {
            set.add(chars[i]);
        }
        int uniqueSize = set.size();

        int left = 0;
        Map<Character, Integer> frequencyMap = new HashMap<>();
        int distinctCount = 0;
        int min = Integer.MAX_VALUE;
        int startIndex = 0;
        for (int right = 0; right < chars.length; right++) {
            char current = chars[right];
            frequencyMap.put(current, frequencyMap.getOrDefault(current, 0) + 1);

            if (frequencyMap.get(current) == 1)
                distinctCount++;
            while (uniqueSize == distinctCount) {
                if ((right - left + 1) < min) {
                    min = right - left + 1;
                    startIndex = left;
                }
                char lc = s.charAt(left);
                frequencyMap.put(lc, frequencyMap.get(lc) - 1);
                if (frequencyMap.get(lc) == 0)
                    distinctCount--; // lost one distinct char
                left++;
            }
        }
        return (min == Integer.MAX_VALUE) ? "" : s.substring(startIndex, startIndex + min);

    }

    public static void main(String[] args) {
        System.out.println(shortestSubstring("dabbcabcd")); // abcd
        System.out.println(shortestSubstring("abcabc")); // abc
        System.out.println(shortestSubstring("abca")); // abc
        System.out.println(shortestSubstring("abcbcba")); // bcb
        System.out.println(shortestSubstring("a")); // a
    }
}
