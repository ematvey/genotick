package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.Application;
import com.alphatica.genotick.genotick.MainSettings;
import com.alphatica.genotick.genotick.TimePoint;

import java.io.Console;

@SuppressWarnings("unused")
class ConsoleInput implements UserInput {
    private final Console console;
    public ConsoleInput() {
        console = System.console();
        if(console == null) {
            throw new RuntimeException("Unable to create system console");
        }
    }

    @Override
    public void show(Application application) {
        String dataDirectory = getString("Data directory", MainSettings.DEFAULT_DATA_DIR);
        MainAppData data = application.createData(dataDirectory);
        MainSettings settings = MainSettings.getSettings(data.getFirstTimePoint(), data.getLastTimePoint());
        settings.startTimePoint = new TimePoint(getLong("Start time point",settings.startTimePoint.getValue()));
        TimePoint nextTimePoint = new TimePoint(settings.startTimePoint.getValue() + 1);
        settings.endTimePoint = new TimePoint(getLong("End time point", nextTimePoint.getValue()));

        settings.populationDAO = getString("Population storage", settings.populationDAO);
        settings.executionOnly = getBoolean("Prediction only", settings.executionOnly);
        settings.processorInstructionLimit = getInteger("Processor instruction limit", settings.processorInstructionLimit);

        if (!settings.executionOnly) {
            settings.dataMaximumOffset = getInteger("Data maximum offset", settings.dataMaximumOffset);
            settings.populationDesiredSize = getInteger("Population desired size", settings.populationDesiredSize);
            settings.maximumDeathByAge = getDouble("Maximum death rate by age", settings.maximumDeathByAge);
            settings.maximumDeathByWeight = getDouble("Maximum death rate by weight", settings.maximumDeathByWeight);
            settings.probabilityOfDeathByAge = getDouble("Probability of death by age", settings.probabilityOfDeathByAge);
            settings.probabilityOfDeathByWeight = getDouble("Probability of death by weight", settings.probabilityOfDeathByWeight);
            settings.inheritedChildWeight = getDouble("Inherited child's weight", settings.inheritedChildWeight);
            settings.protectProgramUntilOutcomes = getInteger("Protect programs until outcomes", settings.protectProgramUntilOutcomes);
            settings.newInstructionProbability = getDouble("Probability of new instruction", settings.newInstructionProbability);
            // This looks like an error but it's not. Default value for 'skipInstruction...' is same as 'newInstruction'
            // to keep programs more or less constant size.
            settings.skipInstructionProbability = getDouble("Probability of skipping instruction", settings.newInstructionProbability);
            settings.instructionMutationProbability = getDouble("Instruction mutation probability", settings.instructionMutationProbability);
            settings.minimumOutcomesToAllowBreeding = getLong("Minimum outcomes to allow breeding", settings.minimumOutcomesToAllowBreeding);
            settings.minimumOutcomesBetweenBreeding = getLong("Minimum outcomes between breeding", settings.minimumOutcomesBetweenBreeding);
            settings.killNonPredictingPrograms = getBoolean("Kill non-predicting programs", settings.killNonPredictingPrograms);
            settings.randomProgramsAtEachUpdate = getDouble("Random programs at each update", settings.randomProgramsAtEachUpdate);
            settings.protectBestPrograms = getDouble("Protect best programs", settings.protectBestPrograms);
            settings.requireSymmetricalPrograms = getBoolean("Require symmetrical programs", settings.requireSymmetricalPrograms);
        }
        application.start(settings,data);
    }

    private double getDouble(String s, double value) {
        String str = String.valueOf(value);
        displayMessage(s,str);
        String response = getString(null,str);
        return Double.parseDouble(response);
    }

    private int getInteger(String message, int def) {
        String str = String.valueOf(def);
        displayMessage(message,str);
        String response = getString(null,str);
        return Integer.parseInt(response);
    }

    private boolean getBoolean(String message, boolean def) {
        String str = String.valueOf(def);
        displayMessage(message, str);
        String response = getString(null,str);
        return Boolean.valueOf(response);
    }

    private long getLong(String message, long def) {
        String str = String.valueOf(def);
        displayMessage(message,str);
        String response = getString(null,str);
        return Long.parseLong(response);
    }

    private String getString(String message, String def) {
        displayMessage(message, def);
        String response = console.readLine();
        if(response.equals(""))
            return def;
        else
            return response;
    }

    private void displayMessage(String message, String def) {
        if(message != null)
            System.out.print(String.format("%s [%s]: ",message,def));
    }
}
