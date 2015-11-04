package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramName;

class ProgramResult {

    private final Prediction prediction;
    private final ProgramName name;
    private final ProgramData data;
    private final Double weight;

    public ProgramResult(Prediction prediction, Program program, ProgramData data) {

        this.prediction = prediction;
        this.name = program.getName();
        this.weight = program.getWeight();
        this.data = data;
    }

    @Override
    public String toString() {
        return "Name: " + name.toString() + " Prediction: " + prediction.toString() + " Weight: " + String.valueOf(weight);
    }

    public ProgramName getName() {
        return name;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public Double getWeight() {
        return weight;
    }

    public ProgramData getData() {
        return data;
    }
}
