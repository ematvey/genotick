package com.alphatica.genotick.population;


import com.alphatica.genotick.genotick.Outcome;
import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.WeightCalculator;
import com.alphatica.genotick.instructions.InstructionList;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class Program implements Serializable {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = -32164665564L;
    private InstructionList mainFunction;
    private ProgramName name;
    private int totalChildren;
    private int totalPredictions;


    private int correctPredictions;
    private double inheritedWeight;
    private static final DecimalFormat weightFormat = new DecimalFormat("0.00");
    private int totalOutcomes;
    private long outcomesAtLastChild;
    private int bias;
    private final int maximumDataOffset;
    private boolean fromParents;

    public void setFromParents() {
        this.fromParents = true;
    }

    public boolean isFromParents() {
        return fromParents;
    }

    public static Program createEmptyProgram(int maximumDataOffset) {
        return new Program(maximumDataOffset);
    }

    public int getLength() {
        return mainFunction.getSize();
    }

    public ProgramName getName() {
        return name;
    }

    public void setInheritedWeight(double inheritedWeight) {
        this.inheritedWeight = inheritedWeight;
    }

    private Program(int maximumDataOffset) {
        mainFunction = InstructionList.createInstructionList();
        this.maximumDataOffset = maximumDataOffset;
    }

    public void recordPrediction(Prediction prediction) {
        if(prediction == Prediction.DOWN)
            bias--;
        else if(prediction == Prediction.UP)
            bias++;
    }
    public void recordOutcomes(List<Outcome> outcomes) {
        for(Outcome outcome: outcomes) {
            totalOutcomes++;
            if(outcome == Outcome.OUT) {
                continue;
            }
            totalPredictions++;
            if(outcome == Outcome.CORRECT)
                correctPredictions++;
        }
    }
    public InstructionList getMainFunction() {
        return mainFunction;
    }


    public double getWeight() {
        double earnedWeight = WeightCalculator.calculateWeight(this);
        return inheritedWeight + earnedWeight;
    }

    public long getOutcomesAtLastChild() {
        return outcomesAtLastChild;
    }

    public void setMainInstructionList(InstructionList newMainFunction) {
        mainFunction = newMainFunction;
    }


    @Override
    public String toString() {
        int length = mainFunction.getSize();
        return "Name: " + this.name.toString()
                + " Outcomes: " + String.valueOf(totalOutcomes)
                + " Weight: " + weightFormat.format(getWeight())
                + " Length: " + String.valueOf(length)
                + " Children: " + String.valueOf(totalChildren);
    }

    public void increaseChildren() {
        totalChildren++;
        outcomesAtLastChild = totalOutcomes;
    }

    public int getMaximumDataOffset() {
        return maximumDataOffset;
    }

    public int getTotalPredictions() {
        return totalPredictions;
    }

    public int getTotalOutcomes() {
        return totalOutcomes;
    }
    public int getCorrectPredictions() {
        return correctPredictions;
    }

    public int getBias() {
        return bias;
    }

    public void setName(ProgramName name) {
        this.name = name;
    }
}
