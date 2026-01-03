import java.util.Arrays;

public class Start {

    public static void main(String args[]) {
        int[] list = { 1, 2, 3, 42, 1, 12, 34, 3 };
        Arrays.stream(Wave.wave(list)).forEach(data-> System.out.println("Started execution " +data ));
        ;
    }
}