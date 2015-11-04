package com.alphatica.genotick.data;


import com.alphatica.genotick.genotick.ProgramData;
import com.alphatica.genotick.genotick.TimePoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSet {
    private final TimePoint[] timePoints;
    private final List<double []> values;
    private final DataSetName name;

    public DataSet(List<List<Number>> lines, String name) {
        this.name = new DataSetName(name);
        timePoints = new TimePoint[lines.size()];
        values = new ArrayList<>();

        final int firstLineCount = lines.get(0).size();
        createValuesArrays(lines.size(),firstLineCount);
        int lineNumber = 0;
        for(List<Number> line: lines) {
            lineNumber++;
            checkNumberOfFieldsInLine(lineNumber,line,firstLineCount);
            fillFirstNumberAsTimePoint(lineNumber, line);
            fillValuesArrays(lineNumber, line, firstLineCount);
        }
    }

    public DataSetName getName() {
        return name;
    }

    public ProgramData getProgramData(TimePoint timePoint) {
        int i = Arrays.binarySearch(timePoints,timePoint);
        if(i < 0) {
            return ProgramData.createEmptyData(name);
        } else {
            return createDataUpToTimePoint(i);
        }
    }

    /**
     * Calculates change in data in the future on first table.
     * @param timePoint timePoint after which change is calculated
     * @return  Double.NaN if no such timePoint or lookAheadPeriod too big. % change otherwise.
     */
    public Double calculateFutureChange(TimePoint timePoint) {
        int i = Arrays.binarySearch(timePoints,timePoint);
        if(i < 0)
            return Double.NaN;
        int startIndex = i + 1;
        int endIndex = startIndex + 1;
        double [] array = values.get(0);
        if(endIndex >= array.length)
            return Double.NaN;
        double startValue = array[startIndex];
        double endValue = array[endIndex];
        return 100.0 *(endValue - startValue) / startValue;
    }

    private void fillValuesArrays(int lineNumber, List<Number> line, int firstLineCount) {
        for(int j = 1; j < firstLineCount; j++)
            values.get(j-1)[lineNumber -1] = line.get(j).doubleValue();
    }

    private void fillFirstNumberAsTimePoint(int lineNumber, List<Number> line) {
        TimePoint timePoint = new TimePoint(line.get(0).intValue());
        // Arrays start indexing from 0, but humans count text lines starting from 1.
        // New timePoint will be assigned to index = lineNumber -1, so
        // we have to check what happened two lines ago!
        if(lineNumber >= 2 &&  timePoint.compareTo(timePoints[lineNumber - 2]) <= 0)
            throw new DataException("Time (first number) is equal or less than previous. Line: " + lineNumber);
        timePoints[lineNumber - 1] = timePoint;
    }

    private void checkNumberOfFieldsInLine(int lineNumber, List<Number> line, int firstLineCount) {
        if(line.size() != firstLineCount)
            throw new DataException("Invalid number of fields in line: " + lineNumber);
    }

    private void createValuesArrays(int size, int firstLineCount) {
        for(int i = 0; i < firstLineCount -1; i++) {
            values.add(new double[size]);
        }
    }

    private ProgramData createDataUpToTimePoint(int i) {
        List<double []> list = new ArrayList<>();
        for(double[] original: values) {
            double [] copy = copyReverseArray(original, i);
            list.add(copy);
        }
        return ProgramData.createData(list,name);
    }

    private double[] copyReverseArray(double[] original, int i) {
        double [] array = new double[i+1];
        for(int k = 0; k <= i; k++)
            array[k] = original[i-k];
        return array;
    }

    public int getLinesCount() {
        return timePoints.length;
    }

    public Number [] getLine(int lineNumber) {
        Number [] line = new Number[1 + values.size()];
        line[0] = timePoints[lineNumber].getValue();
        for(int i = 1; i < line.length; i++) {
            line[i] = values.get(i-1)[lineNumber];
        }
        return line;
    }

    public TimePoint getFirstTimePoint() {
        return timePoints[0];
    }

    public TimePoint getLastTimePoint() {
        return timePoints[timePoints.length -1];
    }
}
