package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class SimpleTimePointExecutor implements TimePointExecutor {

    private DataSetExecutor dataSetExecutor;
    private final ExecutorService executorService;


    public SimpleTimePointExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(cores * 2, new DaemonThreadFactory());
    }

    @Override
    public TimePointResult execute(TimePoint timePoint, List<ProgramData> programDataList, Population population) {
        TimePointResult timePointResult = new TimePointResult(timePoint);
        if(programDataList.isEmpty())
            return timePointResult;
        List<Future<List<ProgramResult>>> tasks = submitTasks(programDataList,population);
        getResults(timePointResult,tasks);
        return timePointResult;
    }

    private void getResults(TimePointResult timePointResult, List<Future<List<ProgramResult>>> tasks) {
        while(!tasks.isEmpty()) {
            try {
                int lastIndex = tasks.size() - 1;
                Future<List<ProgramResult>> future = tasks.get(lastIndex);
                List<ProgramResult> results = future.get();
                tasks.remove(lastIndex);
                for(ProgramResult result: results) {
                    timePointResult.addProgramResult(result);
                }
            } catch (InterruptedException ignore) {
                /* Do nothing, try again */
            } catch (ExecutionException e) {
                Debug.d(e);
            }
        }
    }

    /*
     Return type here is as ugly as it gets and I'm not proud. However, it seems to be the quickest.
     */
    private List<Future<List<ProgramResult>>> submitTasks(List<ProgramData> programDataList, Population population) {
        List<Future<List<ProgramResult>>> tasks = new ArrayList<>();
        for(ProgramName programName: population.listProgramNames()) {
            Task task = new Task(programName, programDataList, population);
            Future<List<ProgramResult>> future = executorService.submit(task);
            tasks.add(future);
        }
        return tasks;
    }

    @Override
    public void setSettings(DataSetExecutor dataSetExecutor) {
        this.dataSetExecutor = dataSetExecutor;
    }

    private class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@SuppressWarnings("NullableProblems") Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

    private class Task implements Callable<List<ProgramResult>> {

        private final ProgramName programName;
        private final List<ProgramData> programDataList;
        private final Population population;

        public Task(ProgramName programName, List<ProgramData> programDataList, Population population) {
            this.programName = programName;
            this.programDataList = programDataList;
            this.population = population;
        }

        @Override
        public List<ProgramResult> call() throws Exception {
            List<ProgramResult> list = new ArrayList<>();
            for(ProgramData programData: programDataList) {
                ProgramResult result = dataSetExecutor.execute(programData,programName,population);
                list.add(result);
            }
            return list;
        }
    }
}
