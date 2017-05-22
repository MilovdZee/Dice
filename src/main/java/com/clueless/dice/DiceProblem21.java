package com.clueless.dice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Throw a number of dice. Then throw all dice that are not 6 until all dice are a six. How many times must I throw on average?
 * 
 * Calculated by Matthew M. Conroy:
 * 1 6
 * 2 8.72727272727273
 * 3 10.5554445554446
 * 4 11.9266962545651
 * 5 13.0236615075553
 * 6 13.9377966973204
 * 7 14.7213415962620
 * 8 15.4069434778816
 * 9 16.0163673664838
 * 10 16.5648488612594
 * 15 18.6998719821123
 * 20 20.2329362496041
 * 30 22.4117651317294
 * 40 23.9670168145374
 * 50 25.1773086926527
 * 
 * @author milo
 *
 */
public class DiceProblem21 {
    private static final int NUMBER_OF_DICE = 50;
    private static final int TOTAL_NUMBER_OF_TRIES = 100000;
    private static final int DECIMALS = 50;

    public static void main(String[] args) throws ExecutionException {

        // Calculate it
        BigDecimal calculated = expectedThrowsCache.get(NUMBER_OF_DICE);
        System.out.println("Calculated number of rounds needed for " + NUMBER_OF_DICE + " dice: " + calculated);
        System.out.println();

        // Do it
        long startTime = System.currentTimeMillis();
        IntStream s = IntStream.range(0, 20);
        s.forEach(i -> {
            long start = System.currentTimeMillis();
            DiceProblem21 diceProblem21 = new DiceProblem21();
            System.out.println("start time " + i);
            diceProblem21.doAction();
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - start;
            System.out.println("run time thread " + i + ": " + elapsedTime + "ms");
        });

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("run time: " + elapsedTime + "ms");
    }

    private void doAction() {
        int totalThrows = 0;
        int tries = TOTAL_NUMBER_OF_TRIES;
        while (tries-- > 0) {
            int rounds = 0;
            int n = NUMBER_OF_DICE;
            do {
                rounds++;
                List<Integer> dice = throwDice(n);
                n -= countDiceOnValue(dice, 6);
            } while (n > 0);
            // System.out.println("Rounds: " + rounds);
            totalThrows += rounds;
        }
        System.out.println("Average number of rounds needed: " + new Double(totalThrows) / new Double(TOTAL_NUMBER_OF_TRIES));
    }

    static LoadingCache<Integer, BigDecimal> expectedThrowsCache = CacheBuilder.newBuilder().build(new CacheLoader<Integer, BigDecimal>() {
        @Override
        public BigDecimal load(Integer n) throws Exception {
            BigDecimal expectedThrows = new BigDecimal(1.0).add(new BigDecimal(5.0).pow(n));
            for (int j = 1; j <= n - 1; j++) {
                expectedThrows = expectedThrows
                        .add(new BigDecimal(1.0).add(expectedThrowsCache.get(j)).multiply(binomialCoefficient(n, n - j))
                                .multiply(new BigDecimal(5.0).pow(j)));
            }
            expectedThrows = expectedThrows.divide(new BigDecimal(6.0).pow(n).subtract(new BigDecimal(5.0).pow(n)), RoundingMode.HALF_UP);
            return expectedThrows.setScale(DECIMALS, RoundingMode.HALF_UP);
        }
    });

    private static int countDiceOnValue(List<Integer> dice, Integer i) {
        int count = (int) dice.stream().filter(n -> n == i).count();
        return count;
    }

    private static List<Integer> throwDice(int numberOfDice) {
        List<Integer> thrown = new ArrayList<>();
        for (int i = 0; i < numberOfDice; i++) {
            int value = (int) (Math.random() * 6.0 + 1.0);
            thrown.add(value);
        }
        return thrown;
    }

    private static BigDecimal binomialCoefficient(int n, int k) {
        BigDecimal binomialCoefficient = new BigDecimal(1.0);
        for (int i = 1; i <= k; i++) {
            binomialCoefficient = binomialCoefficient.multiply(new BigDecimal(n + 1 - i)).divide(new BigDecimal(i), RoundingMode.HALF_UP);
        }
        return binomialCoefficient;
    }
}
