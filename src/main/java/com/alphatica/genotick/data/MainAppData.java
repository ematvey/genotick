package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.ProgramData;
import com.alphatica.genotick.genotick.TimePoint;

import java.util.ArrayList;
import java.util.List;

public class MainAppData {
    private final List<DataSet> sets;

    public MainAppData() {
        sets = new ArrayList<>();
    }

    public void addDataSet(DataSet set) {
        sets.add(set);
    }

    public List<ProgramData> prepareProgramDataList(TimePoint timePoint) {
        List<ProgramData> list = new ArrayList<>();
        for (DataSet set : sets) {
            ProgramData programData = set.getProgramData(timePoint);
            if (programData.isEmpty())
                continue;
            list.add(programData);
        }
        return list;
    }

    public Double getActualChange(DataSetName name, TimePoint timePoint) {
        for(DataSet set: sets) {
            if(!set.getName().equals(name))
                continue;
            return set.calculateFutureChange(timePoint);
        }
        return Double.NaN;
    }

    public TimePoint getFirstTimePoint() {
        if(sets.isEmpty())
            return null;
        TimePoint firstTimePoint = sets.get(0).getFirstTimePoint();
        for(int i = 1; i < sets.size(); i++) {
            TimePoint first = sets.get(i).getFirstTimePoint();
            if(first.compareTo(firstTimePoint) < 0) {
                firstTimePoint = first;
            }
        }
        return firstTimePoint;
    }

    public TimePoint getLastTimePoint() {
        if(sets.isEmpty())
            return null;
        TimePoint lastTimePoint = sets.get(0).getLastTimePoint();
        for(int i = 1; i < sets.size(); i++) {
            TimePoint last = sets.get(i).getLastTimePoint();
            if(last.compareTo(lastTimePoint) > 0) {
                lastTimePoint = last;
            }
        }
        return lastTimePoint;
    }

    public DataSet[] listSets() {
        return sets.toArray(new DataSet[sets.size()]);
    }
}
