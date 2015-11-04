package com.alphatica.genotick.population;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PopulationDAOFileSystem implements PopulationDAO {
    private static final String FILE_EXTENSION = ".prg";
    private String programsPath = "population";
    private final Random random = new Random();

    private List<ProgramName> getAllProgramNames() {
        List<ProgramName> list = new ArrayList<>();
        String [] fileList = listFiles(programsPath);
        if(fileList == null)
            return list;
        for(String name: fileList) {
            String shortName = name.split("\\.")[0];
            Long l = Long.parseLong(shortName);
            list.add(new ProgramName(l));
        }
        return list;
    }

    private String [] listFiles(String dir) {
        File path = new File(dir);
        return path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });
    }
    @Override
    public Iterable<Program> getProgramList() {
        return new Iterable<Program>() {
            class ListAvailablePrograms implements Iterator<Program> {
                final private List<ProgramName> names;
                int index = 0;
                ListAvailablePrograms() {
                    names = getAllProgramNames();
                }
                @Override
                public boolean hasNext() {
                    return names.size() > index;
                }

                @Override
                public Program next() {
                    try {
                        return getProgram(names.get(index++));
                    } catch (IOException | ClassNotFoundException e) {
                        throw new DAOException(e);
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove() not supported");
                }
            }
            @Override
            public Iterator<Program> iterator() {
                return new ListAvailablePrograms();
            }
        };
    }

    private Program getProgram(ProgramName name) throws ClassNotFoundException, IOException {
        File file = new File(programsPath + File.separator + name.toString() + FILE_EXTENSION);
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Program program = (Program) ois.readObject();
            ois.close();
            return program;
        }
    }

    @Override
    public int getAvailableProgramsCount() {
        return getAllProgramNames().size();
    }

    @Override
    public void saveProgram(Program program) {
        if(program.getName() == null) {
            program.setName(getAvailableName());
        }
        File file = createFileForName(program.getName());
        saveProgramToFile(program,file);
    }

    @Override
    public void removeProgram(ProgramName programName) {
        File file = createFileForName(programName);
        boolean result = file.delete();
        if(!result)
            throw new DAOException("Unable to remove file " + file.getAbsolutePath());
    }


    private ProgramName getAvailableName() {
        File file;
        long l;
        do {
            l = random.nextLong();
            if(l < 0)
                l = -l;
            file = new File(programsPath + String.valueOf(l) + FILE_EXTENSION);
        } while (file.exists());
        return new ProgramName(l);
    }

    @Override
    public void setSettings(String pathToDir) {
        File dirFile = new File(pathToDir);
        if(dirFile.exists())
            return;
        boolean success = dirFile.mkdirs();
        if(!success) {
            throw new DAOException("Unable to create dir: " + pathToDir);
        }
        this.programsPath = pathToDir;
    }

    @Override
    public ProgramName[] listProgramNames() {
        String [] files = listFiles(programsPath);
        ProgramName[] names = new ProgramName[files.length];
        for(int i = 0; i < files.length; i++) {
            String longString = files[i].substring(0,files[i].indexOf('.'));
            names[i] = new ProgramName(Long.valueOf(longString));
        }
        return names;
    }


    @Override
    public Program getProgramByName(ProgramName name) {
        File file = createFileForName(name);
        return getProgramFromFile(file);
    }


    private Program getProgramFromFile(File file) {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream( new FileInputStream(file)))) {
            Program program = (Program) ois.readObject();
            ois.close();
            return program;
        } catch (ClassNotFoundException | IOException e) {
            throw new DAOException(e);
        }
    }

    private File createFileForName(ProgramName name) {
        return new File(programsPath + File.separator + name.toString() + FILE_EXTENSION);
    }
    private void saveProgramToFile(Program program, File file)  {
        deleteFileIfExists(file);
        try(ObjectOutputStream ous = new ObjectOutputStream(new BufferedOutputStream( new FileOutputStream(file)))) {
            ous.writeObject(program);
            ous.close();
        } catch (IOException ex) {
            throw new DAOException(ex);
        }
    }

    private void deleteFileIfExists(File file) {
        if(!file.exists())
            return;
        if(!file.delete()) {
            throw new DAOException("Unable to delete file: " + file);
        }
    }


}
