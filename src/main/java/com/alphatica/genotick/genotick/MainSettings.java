package com.alphatica.genotick.genotick;

import java.lang.reflect.Field;

public class MainSettings {
    public static final String DEFAULT_DATA_DIR = "data";

    public TimePoint startTimePoint;
    public TimePoint endTimePoint;
    public String populationDAO = "RAM";
    public boolean executionOnly = false;

    public int populationDesiredSize = 5_000;
    public int processorInstructionLimit = 256;
    public double maximumDeathByAge = 0.01;
    public double maximumDeathByWeight = 0.1;
    public double probabilityOfDeathByAge = 0.5;
    public double probabilityOfDeathByWeight = 0.5;
    public double inheritedChildWeight = 0;
    public int dataMaximumOffset = 256;
    public int protectProgramUntilOutcomes = 100;
    public double newInstructionProbability = 0.01;
    public double instructionMutationProbability = 0.01;
    public double skipInstructionProbability = 0.01;
    public long minimumOutcomesToAllowBreeding = 50;
    public long minimumOutcomesBetweenBreeding = 50;
    public boolean killNonPredictingPrograms = true;
    public double randomProgramsAtEachUpdate = 0.02;
    public double protectBestPrograms = 0.02;
    public boolean requireSymmetricalPrograms = true;

    private MainSettings() {
        /* Empty */
    }
    public static MainSettings getSettings(TimePoint startTimePoint, TimePoint endTimePoint) {
        MainSettings settings = new MainSettings();
        settings.startTimePoint = startTimePoint;
        settings.endTimePoint = endTimePoint;
        return settings;
    }
    public String getString() {
        StringBuilder sb = new StringBuilder();
        Field [] fields = this.getClass().getDeclaredFields();
        for(Field field: fields) {
            try {
                sb.append(field.getName()).append(" ").append(field.get(this)).append("\n");
            } catch (IllegalAccessException e) {
                Debug.d("Unable to print field",field.getName());
            }
        }
        return sb.toString();
    }

    public void validate() {
        ensure(startTimePoint.compareTo(endTimePoint) < 0,
                "Start TimePoint must be lower than End TimePoint");
        ensure(populationDesiredSize > 0, greaterThanZeroString("Population desired size"));
        ensure(dataMaximumOffset > 0, greaterThanZeroString("Data Maximum Offset"));
        ensure(processorInstructionLimit > 0, greaterThanZeroString("Processor Instruction Limit"));
        ensure(checkZeroToOne(maximumDeathByAge), zeroToOneString("Maximum Death by Age"));
        ensure(checkZeroToOne(maximumDeathByWeight), zeroToOneString("Maximum Death by Weight"));
        ensure(checkZeroToOne(probabilityOfDeathByAge), zeroToOneString("Probability Death by Age"));
        ensure(checkZeroToOne(inheritedChildWeight), zeroToOneString("Inherited Child's Weight"));
        ensure(protectProgramUntilOutcomes >= 0, atLeastZeroString("Protect Programs until Outcomes"));
        ensure(checkZeroToOne(newInstructionProbability), zeroToOneString("New Instruction Probability"));
        ensure(checkZeroToOne(instructionMutationProbability), zeroToOneString("Instruction Mutation Probability"));
        ensure(checkZeroToOne(skipInstructionProbability), zeroToOneString("Skip Instruction Probability"));
        ensure(minimumOutcomesToAllowBreeding >= 0, atLeastZeroString("Minimum outcomes to allow breeding"));
        ensure(minimumOutcomesBetweenBreeding >= 0, atLeastZeroString("Minimum outcomes between breeding"));
        ensure(randomProgramsAtEachUpdate >=0, zeroToOneString("Random Programs at Each Update"));
        ensure(protectBestPrograms >= 0, zeroToOneString("Protect Best Programs"));

    }
    private String atLeastZeroString(String s) {
        return s + " must be at least 0";
    }
    private String zeroToOneString(String s) {
        return s + " must be between 0.0 and 1.0";
    }
    private String greaterThanZeroString(String s) {
        return s + " must be greater than 0";
    }
    private boolean checkZeroToOne(double value) {
        return value >= 0 && value <= 1;
    }

    private void ensure(boolean condition, String message) {
        if(!condition) {
            throw new IllegalArgumentException(message);
        }
    }

}
