public class QuickSort {
    private static void sort(int[] data, int low, int high) {
        if (low < high) {
            int pivot = partition(data, low, high);
            sort(data, pivot + 1, high);
            sort(data, low, pivot - 1);
        }

    }

    private static int partition(int[] data, int low, int high) {
        return 1;
    }

    public static void main(String args[]) {

        QuickSort.sort(new int[] { 1, 5, 9, 10, 4 }, 0, 5);

    }
}
