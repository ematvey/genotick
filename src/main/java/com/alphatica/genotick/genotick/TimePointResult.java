package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;

import java.util.HashMap;
import java.util.Map;

class TimePointResult {
    private final Map<DataSetName, DataSetResult> dataSetResultMap;
    private final TimePoint timePoint;

    public TimePointResult(TimePoint timePoint) {
        dataSetResultMap = new HashMap<>();
        this.timePoint = timePoint;
    }

    public TimePoint getTimePoint() {
        return timePoint;
    }

    public void addProgramResult(ProgramResult programResult) {
        DataSetName name = programResult.getData().getName();
        DataSetResult dataSetResult = getDataSetResult(name);
        dataSetResult.addResult(programResult);
    }

    private DataSetResult getDataSetResult(DataSetName name) {
        DataSetResult dataSetResult = dataSetResultMap.get(name);
        if(dataSetResult == null) {
            dataSetResult = new DataSetResult(name);
            dataSetResultMap.put(name,dataSetResult);
        }
        return dataSetResult;
    }

    public DataSetResult[] listDataSetResults() {
        DataSetResult [] array = new DataSetResult[dataSetResultMap.size()];
        int i = 0;
        for(Map.Entry<DataSetName,DataSetResult> entry: dataSetResultMap.entrySet()) {
            array[i++] = entry.getValue();
        }
        return array;
    }
}
