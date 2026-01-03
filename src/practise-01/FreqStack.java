import java.util.*;
import java.util.Stack;

public class FreqStack {
    private Map<Integer, Integer> freqMap;             // val -> frequency
    private Map<Integer, Stack<Integer>> groupMap;     // frequency -> stack of values
    private int maxFreq;

    public FreqStack() {
        freqMap = new HashMap<>();
        groupMap = new HashMap<>();
        maxFreq = 0;
    }

    public void push(int val) {
        int freq = freqMap.getOrDefault(val, 0) + 1;
        freqMap.put(val, freq);
        maxFreq = Math.max(maxFreq, freq);

        groupMap.computeIfAbsent(freq, k -> new Stack<>()).push(val);
    }

    public int pop() {
        Stack<Integer> stack = groupMap.get(maxFreq);
        int val = stack.pop();

        freqMap.put(val, freqMap.get(val) - 1);
        if (stack.isEmpty()) {
            groupMap.remove(maxFreq);
            maxFreq--;
        }

        return val;
    }
}
