package ru.botov.poker.model;

import java.math.BigDecimal;

public class StepPower {

    private BigDecimal power;
    private int step;

    public StepPower(BigDecimal power, int step) {
        this.power = power;
        this.step = step;
    }

    public BigDecimal getPower() {
        return power;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
