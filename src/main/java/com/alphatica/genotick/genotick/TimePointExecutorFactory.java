package com.alphatica.genotick.genotick;

class TimePointExecutorFactory {
    public static TimePointExecutor getDefaultExecutor(DataSetExecutor dataSetExecutor) {
        TimePointExecutor executor = new SimpleTimePointExecutor();
        executor.setSettings(dataSetExecutor);
        return executor;
    }


}
