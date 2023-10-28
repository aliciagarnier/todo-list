package br.edu.unifalmg.repository.impl;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.ChoreRepository;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileChoreRepository implements ChoreRepository  {

    private ObjectMapper objectMapper;

    public FileChoreRepository () {

        objectMapper = new ObjectMapper().findAndRegisterModules();
    }
    @Override
    public List<Chore> load() {

       try {
           return new ArrayList<>(
                   Arrays.asList(
                           objectMapper.readValue(new File("todo/src/main/resources/chores.json"), Chore[].class)
                   )
           );

       } catch (MismatchedInputException exception) {
           System.out.println("Unable to convert the content of the file into chores.");

       } catch (IOException exception) {
           System.out.println("Unable to open file.");
       }
       return new ArrayList<>();
    }

    @Override
    public boolean save(List<Chore> chores) {
        try {
            File file = new File("/home/alicia/Área de Trabalho/Java/todo-list/todo/src/main/resources/chores.json");
            objectMapper.writeValue(file, chores);
            return true;
        } catch (IOException exception) {
            System.out.println("Unable to write the chores on the file.");
        }
        return false;
    }
}
