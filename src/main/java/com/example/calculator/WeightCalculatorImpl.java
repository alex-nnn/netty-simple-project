package com.example.calculator;

import org.springframework.stereotype.Component;

/**
 * Created by remote on 11/12/16.
 */
@Component
public class WeightCalculatorImpl implements WeightCalculator {
    private static int BLOCK_WEIGHT = 50;

    /**
     * @param level
     * @param index
     * @return
     */
    public double getHumanEdgeWeight(int level, int index) {
        checkArguments(level, index);
        double num = getFractionNumerator(level, index);
        //System.out.println("num=" + num);
        long devider = getFractionDenominator(level);
        //System.out.println("devider=" + devider);
        return (num * BLOCK_WEIGHT) / devider;
    }

    private long getFractionDenominator(int level) {
        return pow2(level);
    }

    private long getFractionNumerator(int lvl, int ind) {
        if (2 * ind > lvl) {
            ind = lvl - ind;
        }
        if (ind == 0) {
            return pow2(lvl) - 1;
        }
        int dif = lvl - ind;
        long sum = 0;
        sum += getSumWithoutLeftElements(lvl, ind, dif);
        sum += getSumOfLeftElements(ind, dif);
        return sum;
    }

    private long getSumOfLeftElements(int ind, int dif) {
        long sum = 0;
        int c = 1; //= c^{ind-1}_{ind -1}
        for (int m = 0; m < dif; m++) {
            sum += c * (pow2(dif - m) - 1);//  = c * getFractionNumerator(dif -m, 0) -1
            c = c * (m + ind) / (m + 1);//calculating c^{ind-1}_{m+ind}
        }
        return sum;
    }

    private long getSumWithoutLeftElements(int lvl, int ind, int dif) {
        long sum = 0;
        for (int k = 0; k < ind; k++) {
            int c = 1;
            for (int m = 0; m < dif; m++) {
                sum += c * pow2(lvl - m - k);
                if (k == 0) {
                    continue;
                } else {
                    c = c * (m + k + 1) / (m + 1); //calculating c^{k}_{m+k+1}
                }
            }
            sum += c * pow2(lvl - dif - k - 1);
        }
        return sum;
    }

    private void checkArguments(int level, int index) {
        if (level < index || index < 0) {
            throw new IllegalArgumentException("Arguments are out of scope");
        }
        if (level > 16) {
            throw new IllegalArgumentException("Argument level is too big. Activate the commercial version of the program!");
        }
    }


    private int pow2(int degree) {
        if (degree > 0) {
            return 2<<(degree-1);
        } else if (degree == 0){
            return 1;
        } else {
            return 2>>(-degree-1);
        }

    }
}
