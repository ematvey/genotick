package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.Application;
import com.alphatica.genotick.genotick.MainSettings;

import java.util.Random;

@SuppressWarnings("unused")
class RandomParametersInput implements UserInput {


    @Override
    public void show(Application application) {
        MainAppData data = application.createData(MainSettings.DEFAULT_DATA_DIR);
        MainSettings defaults = MainSettings.getSettings(data.getFirstTimePoint(),data.getLastTimePoint());
        MainSettings appSettings = getSettings(defaults);
        application.start(appSettings, data);
    }

    public MainSettings assignRandom(MainSettings settings) {
        Random r = new Random();
        settings.populationDesiredSize = r.nextInt(5000);
        settings.dataMaximumOffset = r.nextInt(256);
        settings.processorInstructionLimit = r.nextInt(256)+1;
        settings.maximumDeathByAge = r.nextDouble();
        settings.maximumDeathByWeight = r.nextDouble();
        settings.probabilityOfDeathByAge = r.nextDouble();
        settings.probabilityOfDeathByWeight = r.nextDouble();
        settings.inheritedChildWeight = r.nextDouble();
        settings.protectProgramUntilOutcomes = r.nextInt(100);
        settings.protectBestPrograms = r.nextDouble();
        settings.newInstructionProbability = r.nextDouble();
        settings.instructionMutationProbability = r.nextDouble();
        settings.skipInstructionProbability = settings.newInstructionProbability;
        settings.minimumOutcomesToAllowBreeding = r.nextInt(50);
        settings.minimumOutcomesBetweenBreeding = r.nextInt(50);
        settings.randomProgramsAtEachUpdate = r.nextDouble();
        return settings;
    }

    private  MainSettings getSettings(MainSettings defaults) {
        defaults.populationDAO = "RAM";
        defaults.requireSymmetricalPrograms = true;
        defaults.killNonPredictingPrograms = true;
        defaults.executionOnly = false;
        return assignRandom(defaults);
    }

}
