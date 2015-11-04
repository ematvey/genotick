package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;

import java.util.List;

public interface TimePointExecutor {
    TimePointResult execute(TimePoint timePoint, List<ProgramData> programDataList, Population population);

    void setSettings(DataSetExecutor dataSetExecutor);
}
