public class MinHeap {
    int[] heap;
    int capacity;
    int size;

    public MinHeap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.heap = new int[this.capacity];
        this.size = 0;
    }

    public int size() {
        if (this.heap == null || this.size == 0) throw new RuntimeException("Heap not initialized or no elements");
        return this.size;
    }

    public int peek() {
        if (this.heap == null || this.size == 0) throw new RuntimeException("Heap not initialized");
        return heap[0];
    }

    public int offer(int value) {
        ensureCapacity();
        this.heap[size] = value;
        propagateUp(size);
        this.size++;
        return this.size;
    }

    public  int poll() {
        int min = heap[0];
        heap[0] = heap[size-1];
        size --;
        propagateDown(0);
        return min;
    }

    private void propagateDown(int idx) {
        while(true) {
            int left = idx * 2 + 1;
            if(left >= size) break;
            int right = left + 1;
            int smallest = left;
            if(right< size && heap[right] < heap[smallest]) {
                smallest = right;
            }
            if(heap[idx] <= heap[smallest]) {
                 break;
            }
            int temp = heap[smallest];
            heap[smallest] = heap[idx];
            heap[idx] = temp;
            idx = smallest;
        }
    }

    private void propagateUp(int idx) {
        while(true) {
            int parentIndex = idx / 2 - 1;
            int parent = heap[parentIndex];
            if(parentIndex == 0 || parent < heap[idx]) break;
            int temp = heap[idx] ;
            heap[idx] = parent;
            heap[parentIndex] = temp;
        }
    }

    public void ensureCapacity() {
        if (this.size == this.capacity) {
            this.capacity = this.capacity * 2;
            int[] temp = new int[this.capacity];
            if (this.size >= 0) System.arraycopy(heap, 0, temp, 0, this.size);
            heap = temp;
        }
    }
}
