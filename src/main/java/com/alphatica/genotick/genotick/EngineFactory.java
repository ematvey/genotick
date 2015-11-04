package com.alphatica.genotick.genotick;

import com.alphatica.genotick.breeder.ProgramBreeder;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.ProgramKiller;
import com.alphatica.genotick.population.Population;

class EngineFactory {
    public static Engine getDefaultEngine(EngineSettings engineSettings,
                                          MainAppData data, TimePointExecutor timePointExecutor,
                                          ProgramKiller killer,
                                          ProgramBreeder breeder,
                                          Population population) {
        Engine engine = SimpleEngine.getEngine();
        engine.setSettings(engineSettings, timePointExecutor, data, killer, breeder, population);
        return engine;
    }
}