package com.alphatica.genotick.genotick;


import com.alphatica.genotick.population.ProgramExecutorSettings;

class DataSetExecutorFactory {
    public static DataSetExecutor getDefaultSetExecutor(ProgramExecutorSettings settings) {
        DataSetExecutor executor = new SimpleDataSetExecutor();
        executor.setExecutorFactory(settings);
        return executor;
    }
}
