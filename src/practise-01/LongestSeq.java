public class LongestSeq {

    public static int lengthOfLongestSubstring(String s) {
        int n = s.length();
        if (n == 0) {
            return 0;
        }
        int maxLength = 0;
        int charIndex[] = new int[128];
        for (int i = 0, j = 0; j < n; j++) {
            char curr = s.charAt(j);
            i = Math.max(charIndex[curr], i);
            maxLength = Math.max(maxLength, j - i + 1);
            charIndex[curr] = j + 1;
        }
        return maxLength;
    }
}
