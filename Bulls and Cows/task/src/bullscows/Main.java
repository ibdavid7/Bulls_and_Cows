package bullscows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in);
        String secret = generate(in);
        if (secret.length() > 0) {
            playGame(in, secret);
        }
    }

    public static long getCows(String s1, String s2) {
        return s1.chars()
                .distinct()
                .filter(ch -> s2.indexOf(ch) >= 0)
                .count();
    }

    public static long getBulls(String s1, String s2) {
        return IntStream.range(0, s1.length())
                .filter(i -> s1.charAt(i) == s2.charAt(i))
                .count();
    }

    public static void playGame(Scanner in, String secret) {

        int counter = 1;
        long bulls, cows;

        System.out.println("Okay, let's start a game!");

        do {
            System.out.printf("Turn %d:\n", counter++);

            String guess = in.next();
            bulls = getBulls(guess, secret);
            cows = getCows(guess, secret) - bulls;

            if (cows > 0 && bulls > 0) {
                System.out.printf("Grade: %d bull(s) and %d cow(s).\n",
                        bulls,
                        cows,
                        secret);
            } else if (cows == 0 && bulls == 0) {
                System.out.printf("Grade: None.\n");
            } else if (cows > 0) {
                System.out.printf("Grade: %d cow(s).\n",
                        cows);
            } else {
                System.out.printf("Grade: %d bull(s).\n",
                        bulls);
            }

        } while (bulls != secret.length());

        System.out.println("Congratulations! You guessed the secret code.");
    }

    public static String generate(Scanner in) throws Exception {

        try {
            RandomInRanges range = new RandomInRanges();
            range.addRange(48, 57);
            range.addRange(97, 122);

            IntFunction stars = i -> "*".repeat(Math.max(0, i));

            System.out.println("Input the length of the secret code:");
            if (!in.hasNextInt()) {
                throw new Exception(String.format("Error: \"%s\" isn't a valid number.", in.next()));
            }
            int length = in.nextInt();

            if (length > 36) {
                throw new Exception(String.format("Error: maximum number of possible symbols in the code is 36 (0-9, a-z)."));
            }

            if (length < 1) {
                throw new Exception(String.format("Error: Minimum length of the secret code is 1"));
            }

            System.out.println("Input the number of possible symbols in the code:");
            if (!in.hasNextInt()) {
                throw new Exception(String.format("Error: \"%s\" isn't a valid number.", in.next()));
            }
            int symbols = in.nextInt();

            if (symbols < length) {
                throw new Exception(String.format("Error: it's not possible to generate a code with a length of %d with %d unique symbols.", length, symbols));
            }

            if (symbols > range.getLength()) {
                throw new Exception(String.format("Error: it's not possible to generate a code with a length of %d with %d unique symbols.", length, symbols));
            }

            if (symbols < 11) {
                System.out.printf("The secret is prepared: %s (0-%s).\n", stars.apply(length), (char) range.getElement(symbols - 1));
            } else {
                System.out.printf("The secret is prepared: %s (0-9,a-%s).\n", stars.apply(length), (char) range.getElement(symbols - 1));
            }

            return IntStream
                    .iterate(0, x -> x + 1)
                    .mapToObj(i -> (char) range.getRandom(symbols))
//                .peek(System.out::println)
                    .distinct()
                    .limit(length)
                    .collect(Collector.of(StringBuilder::new,
                            StringBuilder::append,
                            StringBuilder::append,
                            StringBuilder::toString));

        } catch (Exception e) {
            System.out.println(e.getMessage());
//            throw e;
        }
        return "";
    }

    static class RandomInRanges {
        private final List<Integer> range = new ArrayList<>();

        RandomInRanges() {
        }

        final void addRange(int min, int max) {
            for (int i = min; i <= max; i++) {
                this.range.add(i);
            }
        }

        int getLength() {
            return this.range.size();
        }

        int getElement(int index) {
            return range.get(index);
        }

        int getRandom(int limitExcl) {
            return this.range.get(new Random().nextInt(limitExcl));
        }
    }
}