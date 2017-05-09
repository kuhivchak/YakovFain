package image;

import java.util.*;

/**
 * Created by Vladik on 07.05.2017.
 */
public class TestConversation extends Test {
    public TestConversation() {
    }
    public static void test() {
        System.out.println(a);

    }

    public static void main(String[] args) {
        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        long start = System.currentTimeMillis();

        System.out.println(calcFibonacci(35));
        System.out.println("Time: " + (System.currentTimeMillis() - start));
        Collections.sort(new ArrayList<TestConversation>(), (o1, o2) -> {
            if (o1.equals(o2)) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public static int max(int[] arr) { // BETTER
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    } //BETTER

    public static int max(Integer[] arr) {
        return Collections.max(Arrays.asList(arr));
    }

    public void sort(int[] array) {
        Arrays.sort(array);
    }

    public static int[] bubbleSort(int[] array) {
        int result[] = new int[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        for (int i = 0; i < result.length - 1; i++) {
            for (int j = 0; j < result.length - i - 1; j++) {
                if (result[i] > result[j]) {
                    int tmp = result[i];
                    result[i] = result[j];
                    result[j] = tmp;
                }
            }
        }
        return result;
    }

    public static Integer[] bubbleSort(Integer[] array) { // BETTER
        List<Integer> list = Arrays.asList(array);
        Collections.sort(list);
        return (Integer[]) list.toArray();
    } //BETTER

    public static int calcFibonacci(int number) {
        if (number <= 0) {
            throw new RuntimeException();
        }
        int first = 1;
        int second = 1;

        for (int i = 2; i < number; i++) {
            int sum = first + second;
            first = second;
            second = sum;
        }
        return second;
    }

    public static int calcFibonacciRec(int number) {
        if (number == 0) {
            return 1;
        } else
        return calcFibonacci(number-1)+calcFibonacci(number-2);
    } //BETTER
}
