package com.alphatica.genotick.genotick;

import com.alphatica.genotick.breeder.ProgramBreeder;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.killer.ProgramKiller;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class SimpleEngine implements Engine {
    private EngineSettings engineSettings;
    private TimePointExecutor timePointExecutor;
    private ProgramKiller killer;
    private ProgramBreeder breeder;
    private Population population;
    private MainAppData data;

    private SimpleEngine() {
    }
    public static Engine getEngine() {
        return new SimpleEngine();
    }

    @Override
    public List<TimePointStats> start() {
        double result = 1;
        initPopulation();
        TimePoint timePoint = new TimePoint(engineSettings.startTimePoint);
        List<TimePointStats> timePointStats = new ArrayList<>();
        while(engineSettings.endTimePoint.compareTo(timePoint) >= 0) {
            TimePointStats stat = executeTimePoint(timePoint);
            timePointStats.add(stat);
            result *= (stat.getPercentEarned() / 100 + 1);
            Debug.d("Time:",timePoint,"Percent earned so far:",(result - 1) * 100);
            timePoint.increment();
        }
        if(!engineSettings.executionOnly) {
            population.savePopulation(getSavedPopulationDirName());
        }
        return timePointStats;
    }

    @Override
    public void setSettings(EngineSettings engineSettings,
                            TimePointExecutor timePointExecutor,
                            MainAppData data,
                            ProgramKiller killer,
                            ProgramBreeder breeder,
                            Population population) {
        this.engineSettings = engineSettings;
        this.timePointExecutor = timePointExecutor;
        this.killer = killer;
        this.breeder = breeder;
        this.population = population;
        this.data = data;
    }

    private String getSavedPopulationDirName() {
        String prefix = "savedPopulation_";
        DateFormat format = new SimpleDateFormat("yyyy_MM_dd_kk_mm");
        return prefix + format.format(Calendar.getInstance().getTime());
    }

    private void initPopulation() {
        if(population.getSize() == 0 && !engineSettings.executionOnly)
            breeder.breedPopulation(population);
    }

    private TimePointStats executeTimePoint(TimePoint timePoint) {
        Debug.d("Starting TimePoint:", timePoint);
        List<ProgramData> programDataList = data.prepareProgramDataList(timePoint);
        TimePointResult timePointResult = timePointExecutor.execute(timePoint,programDataList, population);
        TimePointStats timePointStats = TimePointStats.getNewStats(timePoint);
        for(DataSetResult dataSetResult: timePointResult.listDataSetResults()) {
            Prediction prediction = dataSetResult.getCumulativePrediction();
            Debug.d("Prediction for:",dataSetResult.getName(),prediction);
            Double actualChange = data.getActualChange(dataSetResult.getName(),timePoint);
            if(!actualChange.isNaN()) {
                Debug.d("Actual change:", actualChange);
                printPercentEarned(dataSetResult.getName(), prediction, actualChange);
                timePointStats.update(dataSetResult.getName(), actualChange, prediction);
            }
        }
        if(!engineSettings.executionOnly && !programDataList.isEmpty() && !timePointStats.isEmpty())
            updatePopulation(timePointResult);
        printParentsVsRandomStats();
        Debug.d("Finished TimePoint:",timePoint);
        return timePointStats;
    }

    private void printParentsVsRandomStats() {
        int fromParentsCount = 0;
        int randomCount = 0;
        double fromParentsWeight = 0.0;
        double randomWeight = 0.0;

        for(Program program: population.listPrograms()) {
            if(program.isFromParents()) {
                fromParentsCount++;
                fromParentsWeight += Math.abs(program.getWeight());
            } else {
                randomCount++;
                randomWeight += Math.abs(program.getWeight());
            }
        }
        double ratio;
        if(randomCount > 0) {
            ratio = (double) fromParentsCount / (double) randomCount;
            Debug.d("Ratio of programs from parents vs random:",ratio);
        }
        if(fromParentsCount > 0)
            Debug.d("Average program-from-parents weight:",fromParentsWeight / fromParentsCount);
        if(randomCount > 0)
            Debug.d("Average random-program weight:",randomWeight / randomCount);
    }

    private void printPercentEarned(DataSetName name, Prediction prediction, Double actualChange) {
        double percent;
        if(prediction == Prediction.OUT) {
            Debug.d("No position");
            return;
        }
        if(prediction.isCorrect(actualChange))
            percent = Math.abs(actualChange);
        else
            percent = -Math.abs(actualChange);
        Debug.d("Profit for",name,percent);
    }

    private void updatePopulation(TimePointResult timePointResult) {
        HashMap<ProgramName, List<Outcome>> programPredictions = new HashMap<>();
        for(DataSetResult dataSetResult: timePointResult.listDataSetResults()) {
            Double actualChange = data.getActualChange(dataSetResult.getName(),timePointResult.getTimePoint());
            updateProgramPredictions(programPredictions,dataSetResult,actualChange);
        }
        updateProgramResults(programPredictions);
        killer.killPrograms(population);
        breeder.breedPopulation(population);
    }

    private void updateProgramResults(Map<ProgramName, List<Outcome>> programResults) {
        if(programResults.isEmpty())
            return;
        for(Map.Entry<ProgramName, List<Outcome>> entry: programResults.entrySet()) {
            Program program = population.getProgram(entry.getKey());
            program.recordOutcomes(entry.getValue());
            population.saveProgram(program);
        }
    }

    private void updateProgramPredictions(HashMap<ProgramName, List<Outcome>> programPredictions, DataSetResult dataSetResult,double actualChange) {
        for(ProgramResult programResult: dataSetResult.listProgramResults()) {
            List<Outcome> list = getListForProgram(programResult.getName(), programPredictions);
            Outcome outcome = Outcome.getOutcome(programResult.getPrediction(), actualChange);
            list.add(outcome);
        }
    }

    private List<Outcome> getListForProgram(ProgramName name, HashMap<ProgramName, List<Outcome>> programPredictions) {
        List<Outcome> list = programPredictions.get(name);
        if(list == null) {
            list = new ArrayList<>();
            programPredictions.put(name,list);
        }
        return list;
    }

}
