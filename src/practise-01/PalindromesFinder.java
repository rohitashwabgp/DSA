import java.util.*;

public class PalindromesFinder {

    // Return all palindromic substrings (including duplicates for different positions)
    public static List<String> allPalindromicSubstrings(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.length() == 0) return result;
        for (int center = 0; center < s.length(); center++) {
            // odd-length
            expandAndCollect(s, center, center, result);
            // even-length
            expandAndCollect(s, center, center + 1, result);
        }
        return result;
    }


    private static void expandAndCollect(String s, int left, int right, List<String> out) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            out.add(s.substring(left, right + 1));
            left--; right++;
        }
    }

    // quick demo
    public static void main(String[] args) {
        String s = "ababa";
        System.out.println("All palindromic substrings (with duplicates):");
        System.out.println(allPalindromicSubstrings(s));
        System.out.println("Unique palindromic substrings:");
        System.out.println(uniquePalindromicSubstrings(s));
    }
}
