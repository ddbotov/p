package ru.botov.poker.model;

import java.math.BigDecimal;

public class StepPower {

    private BigDecimal power;
    private int stepPower;

    public StepPower(BigDecimal power, int stepPower) {
        this.power = power;
        this.stepPower = stepPower;
    }

    public BigDecimal getPower() {
        return power;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public int getStepPower() {
        return stepPower;
    }

    public void setStepPower(int stepPower) {
        this.stepPower = stepPower;
    }
}
