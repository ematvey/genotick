package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.Debug;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSystemDataLoader implements DataLoader {
    private final String path;
    private final String extension = ".csv";

    public FileSystemDataLoader(String args) {
        path = args;
    }

    @Override
    public MainAppData createProgramData() throws DataException {
        return loadData();
    }

    private MainAppData loadData() {
        MainAppData data = new MainAppData();
        String[] names = listFiles();
        for (String name : names) {
            Debug.d("Reading file", name);
            data.addDataSet(createDataSet(name));
        }
        return data;

    }
    private DataSet createDataSet(String name) {
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path + File.separator + name)))) {
            List<List<Number>> lines = createLineList(br);
            br.close();
            Debug.d("Got",lines.size(),"lines");
            return new DataSet(lines,name);
        } catch (IOException  | DataException e) {
            DataException de = new DataException("Unable to process file: " + name);
            de.initCause(e);
            throw de;
        }
    }

    private List<List<Number>> createLineList(BufferedReader br) {
        List<List<Number>> list = new ArrayList<>();
        int linesRead = 1;
        try {
            String line;
            while ((line = br.readLine())!=null){
                List<Number> lineList = processLine(line);
                list.add(lineList);
                linesRead++;
            }
            return list;
        } catch(IOException | NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            DataException de = new DataException("Error reading line " + linesRead);
            de.initCause(ex);
            throw de;
        }
    }


    private List<Number> processLine(String line) {
        String separator = ",";
        String[] fields = line.split(separator);
        List<Number> list = new ArrayList<>(fields.length);
        list.add(Long.valueOf(fields[0]));
        for(int i = 1; i < fields.length; i++) {
            list.add(Double.valueOf(fields[i]));
        }
        return list;
    }

    private String[] listFiles() {
        String [] list = new File(path).list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(extension);
            }
        });
        if(list == null) {
            throw new DataException("Unable to list files in " + path);
        }
        return list;
    }
}


