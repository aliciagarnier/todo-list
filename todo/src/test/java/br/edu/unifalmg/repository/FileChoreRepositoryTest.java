package br.edu.unifalmg.repository;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.service.ChoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileChoreRepositoryTest {
    @InjectMocks
    private FileChoreRepository repository;

    @Mock
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("#load > When the file is found > When the content is empty > Return empty list")
    void loadWhenTheFileIsFoundWhenTheContentIsEmptyReturnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("todo/src/main/resources/chores.json"), Chore[].class)
        ).thenThrow(MismatchedInputException.class);

        List<Chore> response = repository.load();

        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is not found (or path is invalid) > Return an empty list")
    void loadWhenTheFileIsNotFoundOrPathIsInvalidReturnAnEmptyList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("todo/src/main/resources/chores.json"), Chore[].class)
        ).thenThrow(FileNotFoundException.class);

        List<Chore> response = repository.load();
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#load > When the file is loaded > Return a chores list")
    void loadWhenTheFileIsLoadedReturnAChoresList() throws IOException {
        Mockito.when(
                mapper.readValue(new File("todo/src/main/resources/chores.json"), Chore[].class)
        ).thenReturn(new Chore[] {
                new Chore("First Chore", Boolean.FALSE, LocalDate.now()),
                new Chore("Second Chore", Boolean.TRUE, LocalDate.now().minusDays(5))
        });

        List<Chore> chores = repository.load();

        assertAll(
                () -> assertEquals(2, chores.size()),
                () -> assertEquals("First Chore", chores.get(0).getDescription()),
                () -> assertEquals(LocalDate.now().minusDays(5), chores.get(1).getDeadline())
        );
    }

    @Test
    @DisplayName("#save > When the file is found > When the list is not empty > Save all the chores on json file and return true")
    void saveWhenTheListIsNotEmptySaveTheChores() throws IOException {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());

        Mockito.doNothing().when(mapper).writeValue(new File("todo/src/main/resources/chores.json"),
                service.getChores());

        assertTrue(repository.saveAll(service.getChores()));

        Mockito.when(
                mapper.readValue(new File("todo/src/main/resources/chores.json"), Chore[].class)
        ).thenReturn(new Chore[] {
                new Chore("Chore #01", Boolean.FALSE, LocalDate.now()),
        });

        List<Chore> response = repository.load();

        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(LocalDate.now(), response.get(0).getDeadline()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#save > When the file is found > When the list is empty > Save nothing on json file and return true")
    void saveWhenTheFileIsFoundAndTheListIsEmpty() throws IOException {

        ChoreService service = new ChoreService();

        Mockito.doNothing().when(mapper).writeValue(new File("todo/src/main/resources/chores.json"),
                service.getChores());

        assertTrue(repository.saveAll(service.getChores()));

        Mockito.when(
                mapper.readValue(new File("todo/src/main/resources/chores.json"), Chore[].class)
        ).thenThrow(MismatchedInputException.class);

        List<Chore> response = repository.load();

        assertTrue(response.isEmpty());
    }
    @Test
    @DisplayName("#save > When the file is not found (or path is invalid) > Return false")
    void saveWhenTheFileIsNotFoundOrPathIsInvalidReturnFalse() throws IOException {
        ChoreService service = new ChoreService();
        Mockito.doThrow(IOException.class).when(mapper).writeValue(new File("todo/src/main/resources/chores.json"),
                service.getChores());
        assertFalse(service.saveChores());
    }

}
