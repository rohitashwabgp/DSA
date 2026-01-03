import java.util.*;

public class CombinePalindromesCustom {

    // same helpers as before:
    public static String longestPalindromeChoice(String s) {
        if (s == null || s.isEmpty()) return "";
        String best = s.substring(0,1);
        int n = s.length();

        for (int center = 0; center < n; center++) {
            String odd = expandAround(s, center, center);
            best = chooseBetter(best, odd);
            if (center + 1 < n) {
                String even = expandAround(s, center, center + 1);
                best = chooseBetter(best, even);
            }
        }
        return best;
    }

    private static String expandAround(String s, int left, int right) {
        int n = s.length();
        while (left >= 0 && right < n && s.charAt(left) == s.charAt(right)) {
            left--; right++;
        }
        return s.substring(left + 1, right);
    }

    private static String chooseBetter(String a, String b) {
        if (a == null || a.isEmpty()) return b;
        if (b == null || b.isEmpty()) return a;
        if (b.length() > a.length()) return b;
        if (b.length() < a.length()) return a;
        return (b.compareTo(a) < 0) ? b : a;
    }

    private static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    // New: build palindrome using user's rule
    public static String buildCustomPalindrome(String p1, String p2) {
        if (p1 == null) p1 = "";
        if (p2 == null) p2 = "";

        // compute prefixes (floor(len/2)) and middle chars (if odd length)
        int half1 = p1.length() / 2;
        String pref1 = p1.substring(0, Math.min(half1, p1.length()));
        String mid1 = (p1.length() % 2 == 1) ? p1.substring(half1, half1 + 1) : "";

        int half2 = p2.length() / 2;
        String pref2 = p2.substring(0, Math.min(half2, p2.length()));
        String mid2 = (p2.length() % 2 == 1) ? p2.substring(half2, half2 + 1) : "";

        // choose middle char according to lexicographic rule
        String midChosen = "";
        if (!mid1.isEmpty() && !mid2.isEmpty()) {
            midChosen = (mid1.compareTo(mid2) <= 0) ? mid1 : mid2;
        } else if (!mid1.isEmpty()) {
            midChosen = mid1;
        } else if (!mid2.isEmpty()) {
            midChosen = mid2;
        } // else stay empty

        // construct: pref1 + pref2 + midChosen + reverse(pref2) + reverse(pref1)
        return pref1 + pref2 + midChosen + reverse(pref2) + reverse(pref1);
    }

    // demo main
    public static void main(String[] args) {
        String s1 = "abacdfgdcaba";   // -> longest palindrome "aba"
        String s2 = "racecarxyzracecar"; // -> "racecar"

        String p1 = longestPalindromeChoice(s1);
        String p2 = longestPalindromeChoice(s2);

        System.out.println("p1 = \"" + p1 + "\"");
        System.out.println("p2 = \"" + p2 + "\"");

        String custom = buildCustomPalindrome(p1, p2);
        System.out.println("Custom palindrome = \"" + custom + "\"");
        // For the example, prints: "aracbcara"
    }
}
