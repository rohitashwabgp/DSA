import java.util.Arrays;

public class Wave {

    public static int[] wave(int[] list) {
        Arrays.sort(list);
        for (int i = 0; i < list.length - 1; i = i+2) {
            int temp = list[i];
            list[i] = list[i + 1];
            list[i + 1] = temp;
        }
        return list;
    }
}
