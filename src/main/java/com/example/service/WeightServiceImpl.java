package com.example.service;

import com.example.calculator.WeightCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by remote on 11/14/16.
 */
@Component
public class WeightServiceImpl implements  WeightService {
    @Autowired
    private WeightCalculator weightCalculator;

    @Override
    public double getHumanEdgeWeight(int level, int index) {
        return weightCalculator.getHumanEdgeWeight(level, index);
    }
}
