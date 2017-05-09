package image;

import java.util.*;

/**
 * Created by Vladik on 07.05.2017.
 */
public class Test {
    public static int a = 5;
    static {
        System.out.println("lol");
    }
    public static void test() {
        System.out.println("test");
        Set<Integer> set = new HashSet<>();
        String s;
        set.add(3);
        set.add(3);

        Queue<Integer> q = new ArrayDeque<>();
        Queue<Integer> q1 = new PriorityQueue<>();
        Collections c;
    }

    public static void main(String[] args) {

        TreeMap<TestConversation, String> map = new TreeMap<>();
        map.put(new TestConversation(),"lol");
    }

    public static int max(Integer[] arr) { // BETTER
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    } //BETTER
}
