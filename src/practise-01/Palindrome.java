public class Palindrome {

    public static String find(String original) {

        String longest = "";
        for (int i = 0; i < original.length(); i++) {
            int left = i;
            int right = i;
            int evenRight = i + 1;
            int evenLeft = i;
            while (left >= 0 && right < original.length()) {
                if (original.charAt(left) == original.charAt(right)) {
                    left--;
                    right++;
                } else {
                    break;
                }
            }

            while (evenLeft >= 0 && evenRight < original.length()) {
                if (original.charAt(evenLeft) == original.charAt(evenRight)) {
                    evenLeft--;
                    evenRight++;
                } else {
                    break;
                }
            }
            if ((right - left - 1) > longest.length()) {
                longest = original.substring(left + 1, right);
            } 
             if ((evenRight - evenLeft - 1) > longest.length())  {
                longest = original.substring(evenLeft + 1, evenRight);
            }

        }
        return longest;

    }
}
