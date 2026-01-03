import java.util.ArrayList;
import java.util.List;

public class Sequenece {

    public static int longest(int[] list) {

        List<Integer> tails = new ArrayList<>();

        for (int ind = 0; ind < list.length; ind++) {
            int left = 0;
            int right = tails.size();

            while (left < right) {
                int mid = left + (right - left) / 2;

                if (tails.get(mid) > list[ind]) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }

            if (tails.size() == left) {
                tails.add(list[ind]);
            } else {
                tails.set(left, list[ind]);
            }
        }

        return tails.size();
    }
}
