import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> textOf_A = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textOf_B = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> textOf_C = new ArrayBlockingQueue<>(100);
    public static Thread newText_ABC;

    public static void main(String[] args) throws InterruptedException {
        newText_ABC = new Thread(() -> {
            for (int i = 0; i < 10_000; i++){
                String text = generateText("abc", 100_000);
                try {
                    textOf_A.put(text);
                    textOf_B.put(text);
                    textOf_C.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        newText_ABC.start();

        Thread thread_A = newThread(textOf_A, 'a');
        Thread thread_B = newThread(textOf_B, 'b');
        Thread thread_C = newThread(textOf_C, 'c');
        thread_A.start();
        thread_B.start();
        thread_C.start();
        thread_A.join();
        thread_B.join();
        thread_C.join();

    }

    public static int maxAmount(BlockingQueue<String> text, char letter) throws InterruptedException {
        int cnt = 0;
        int max = 0;
        String newText;

        while (newText_ABC.isAlive()){
            newText = text.take();
            for (char ch : newText.toCharArray()){
                if (ch == letter){cnt ++;}
            }
            if (cnt > max){max = cnt;}
            cnt = 0;
        }
        return max;
    }

    public static Thread newThread(BlockingQueue<String> text, char letter){
        return new Thread(() -> {
            int max = 0;
            try {
                max = maxAmount(text, letter);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Максимальное кол-во '" + letter + "' : " + max);
        });
    }

    public static String generateText(String letters, int length){
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}