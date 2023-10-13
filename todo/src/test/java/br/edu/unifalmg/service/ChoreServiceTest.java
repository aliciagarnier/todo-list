package br.edu.unifalmg.service;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.enumerator.ChoreFilter;
import br.edu.unifalmg.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChoreServiceTest {

    @Test
    @DisplayName("#addChore > When the description is invalid > Throw an exception")
    void addChoreWhenTheDescriptionIsInvalidThrowAnException() {
        ChoreService service = new ChoreService();
        assertAll(
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", null)),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().plusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore(null, LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDescriptionException.class,
                        () -> service.addChore("", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When the deadline is invalid > Throw an exception")
    void addChoreWhenTheDeadlineIsInvalidThrowAnException() {
        ChoreService service = new ChoreService();
        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", null)),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.addChore("Description", LocalDate.now().minusDays(1)))
        );
    }

    @Test
    @DisplayName("#addChore > When adding a chore > When the chore already exists > Throw an exception")
    void addChoreWhenAddingAChoreWhenTheChoreAlreadyExistsThrowAnException() {
        ChoreService service = new ChoreService();
        service.addChore("Description", LocalDate.now());
        assertThrows(DuplicatedChoreException.class,
                () -> service.addChore("Description", LocalDate.now()));
    }

    @Test
    @DisplayName("#addChore > When the chore's list is empty > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListIsEmptyWhenAddingANewChoreAddTheChore() {
        ChoreService service = new ChoreService();
        Chore response = service.addChore("Description", LocalDate.now());
        assertAll(
                () -> assertEquals("Description", response.getDescription()),
                () -> assertEquals(LocalDate.now(), response.getDeadline()),
                () -> assertEquals(Boolean.FALSE, response.getIsCompleted())
        );
    }

    @Test
    @DisplayName("#addChore > When the chore's list has at least one element > When adding a new chore > Add the chore")
    void addChoreWhenTheChoresListHasAtLeastOneElementWhenAddingANewChoreAddTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        service.addChore("Chore #02", LocalDate.now().plusDays(2));
        assertAll(
                () -> assertEquals(2, service.getChores().size()),
                () -> assertEquals("Chore #01", service.getChores().get(0).getDescription()),
                () -> assertEquals(LocalDate.now(), service.getChores().get(0).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(0).getIsCompleted()),
                () -> assertEquals("Chore #02", service.getChores().get(1).getDescription()),
                () -> assertEquals(LocalDate.now().plusDays(2), service.getChores().get(1).getDeadline()),
                () -> assertEquals(Boolean.FALSE, service.getChores().get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#deleteChore > When the list is empty > Throw an exception")
    void deleteChoreWhenTheListIsEmptyThrowAnException() {
        ChoreService service = new ChoreService();
        assertThrows(EmptyChoreListException.class, () -> {
            service.deleteChore("Qualquer coisa", LocalDate.now());
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore does not exist > Throw an exception")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreDoesNotExistThrowAnException() {
        ChoreService service = new ChoreService();
        service.addChore("Description", LocalDate.now());
        assertThrows(ChoreNotFoundException.class, () -> {
            service.deleteChore("Chore to be deleted", LocalDate.now().plusDays(5));
        });
    }

    @Test
    @DisplayName("#deleteChore > When the list is not empty > When the chore exists > Delete the chore")
    void deleteChoreWhenTheListIsNotEmptyWhenTheChoreExistsDeleteTheChore() {
        ChoreService service = new ChoreService();

        service.addChore("Chore #01", LocalDate.now().plusDays(1));
        assertEquals(1, service.getChores().size());

        assertDoesNotThrow(() -> service.deleteChore("Chore #01", LocalDate.now().plusDays(1)));
        assertEquals(0, service.getChores().size());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsValidToggleTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is valid > When toggle the chore twice > Toggle chore")
    void toggleChoreWhenTheDeadlineIsValidWhenToggleTheChoreTwiceToggleTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertTrue(service.getChores().get(0).getIsCompleted());

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now()));

        assertFalse(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the chore does not exist > Throw an exception")
    void toggleChoreWhenTheChoreDoesNotExistThrowAnException() {
        ChoreService service = new ChoreService();
        assertThrows(ChoreNotFoundException.class, () -> service.toggleChore("Chore #01", LocalDate.now()));
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When the status is uncompleted > Toggle the chore")
    void toggleChoreWhenTheDeadlineIsInvalidWhenTheStatusInUncompletedToggleTheChore() {
        ChoreService service = new ChoreService();
        service.addChore("Chore #01", LocalDate.now());
        assertFalse(service.getChores().get(0).getIsCompleted());
        service.getChores().get(0).setDeadline(LocalDate.now().minusDays(1));

        assertDoesNotThrow(() -> service.toggleChore("Chore #01", LocalDate.now().minusDays(1)));
        assertTrue(service.getChores().get(0).getIsCompleted());
    }

    @Test
    @DisplayName("#toggleChore > When the deadline is invalid > When status is completed > Throw an exception")
    void toggleChoreWhenTheDeadlineIsInvalidWhenStatusIsCompletedThrowAnException() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().minusDays(1)));

        assertThrows(ToggleChoreWithInvalidDeadlineException.class, () ->
                service.toggleChore("Chore #01", LocalDate.now().minusDays(1))
        );
    }
    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is empty > Return all chores")
    void filterChoresWhenTheFilterIsAllWhenTheListIsEmptyReturnAllChores() {
        ChoreService service = new ChoreService();
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is ALL > When the list is not empty > Return all chores")
    void filterChoresWhenTheFilterIsAllWhenTheListIsNotEmptyReturnAllChores() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.ALL);
        assertAll(
            () -> assertEquals(2, response.size()),
            () -> assertEquals("Chore #01", response.get(0).getDescription()),
            () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted()),
            () -> assertEquals("Chore #02", response.get(1).getDescription()),
            () -> assertEquals(Boolean.TRUE, response.get(1).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsEmptyReturnAnEmptyList() {
        ChoreService service = new ChoreService();
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is COMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsCompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.COMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #02", response.get(0).getDescription()),
                () -> assertEquals(Boolean.TRUE, response.get(0).getIsCompleted())
        );
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is empty > Return an empty list")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsEmptyReturnAnEmptyList() {
        ChoreService service = new ChoreService();
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("#filterChores > When the filter is UNCOMPLETED > When the list is not empty > Return the filtered chores")
    void filterChoresWhenTheFilterIsUncompletedWhenTheListIsNotEmptyReturnTheFilteredChores() {
        ChoreService service = new ChoreService();
        service.getChores().add(new Chore("Chore #01", Boolean.FALSE, LocalDate.now()));
        service.getChores().add(new Chore("Chore #02", Boolean.TRUE, LocalDate.now()));
        List<Chore> response = service.filterChores(ChoreFilter.UNCOMPLETED);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals("Chore #01", response.get(0).getDescription()),
                () -> assertEquals(Boolean.FALSE, response.get(0).getIsCompleted())
        );
    }
    @Test
    @DisplayName("#displayAllChores > When the list is not empty > Display all chores")
     void printChoresWhenTheListIsNotEmptyPrintAllChores() {
        ChoreService service = new ChoreService();

        service.getChores().add(new Chore("Chore #01", Boolean.TRUE, LocalDate.now().plusDays(1)));
        service.getChores().add(new Chore("Chore #02", Boolean.FALSE, LocalDate.now().plusDays(2)));

        String expectedOutput = "Description: Chore #01 Deadline: " + LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " Status: Completed\n" +
                "Description: Chore #02 Deadline: " + LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " Status: Not completed";

        String obtainedOutput = service.printChores();

        assertEquals(expectedOutput, obtainedOutput);
    }
    @Test
    @DisplayName("#displayAllChores > When the list is empty > throw EmptyChoreListException")
    void printChoresWhenTheListIsEmptyThrowAnException() {
        ChoreService service = new ChoreService();
        assertThrows(EmptyChoreListException.class, service::printChores);
    }
    @Test
    @DisplayName("#editChore > Edit chore description when the description is valid > description edited successfully")
    void editChoreDescriptionWhenTheDescriptionIsValid() {
        ChoreService service = new ChoreService();
        service.addChore("Chore 1", LocalDate.now().plusDays(1));

        service.editChore("Chore 1", LocalDate.now().plusDays(1), "New description");

        assertEquals("New description", service.getChores().get(0).getDescription());
        assertEquals(LocalDate.now().plusDays(1), service.getChores().get(0).getDeadline());

    }
    @Test
    @DisplayName("#editChore > Edit chore deadline when the deadline is valid > deadline edit sucessfully")
    void editChoreDeadlineWhenTheDeadlineIsValid() {
        ChoreService service = new ChoreService();
        service.addChore("Chore 1", LocalDate.now().plusDays(1));
        service.editChore("Chore 1",  LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        assertEquals(LocalDate.now().plusDays(2), service.getChores().get(0).getDeadline());
        assertEquals("Chore 1", service.getChores().get(0).getDescription());

    }
    @Test
    @DisplayName("#editChore > Edit chore when the description is invalid > Throw an exception")
    void editChoreDescriptionWhenTheDescriptionIsInvalidThrowAnException() {
        ChoreService service = new ChoreService();
        service.addChore("Chore 1", LocalDate.now());

        assertAll(
                () -> assertThrows(InvalidDescriptionException.class, () -> service.editChore("Chore 1",
                        LocalDate.now(), "")),
                () -> assertThrows(InvalidDescriptionException.class, () -> service.editChore("Chore 1",
                        LocalDate.now(),  (String) null))
                );
    }
    @Test
    @DisplayName("#editChore > Edit chore deadline when the deadline is invalid > Throw an exception")
    void editChoreDeadlineWhenTheDeadlineIsInvalidThrowAnException () {
        ChoreService service = new ChoreService();
        service.addChore("Chore 1", LocalDate.now().plusDays(1));

        assertAll(
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.editChore("Chore 1", LocalDate.now().plusDays(1), LocalDate.now().minusDays(1))),
                () -> assertThrows(InvalidDeadlineException.class,
                        () -> service.editChore("Chore 1", LocalDate.now().plusDays(1),  (LocalDate) null))
                );
    }
    @Test
    @DisplayName("#editChore > Edit chore description when the description is valid but the chore already exists > Throw an exception")
    void editChoreDescriptionWhenTheDescriptionIsValidChoreAlreadyExistsThrowAnException () {
        ChoreService service = new ChoreService();

        service.addChore("Chore 1", LocalDate.now().plusDays(1));
        service.addChore("Chore 2", LocalDate.now().plusDays(1));

        assertAll(
                () -> assertThrows(DuplicatedChoreException.class,
                        () -> service.editChore("Chore 1", LocalDate.now().plusDays(1), "Chore 2")),
                () -> assertThrows(DuplicatedChoreException.class,
                        () -> service.editChore("Chore 1", LocalDate.now().plusDays(1), "Chore 1"))
                );
    }
    @Test
    @DisplayName("#editChore > Edit chore deadline when the deadline is valid but the chore already exists > Throw an exception")
    void editChoreDeadlineWhenTheDeadlineIsValidChoreAlreadyExistsThrowAnException () {
        ChoreService service = new ChoreService();

        service.addChore("Research", LocalDate.now().plusDays(7));
        service.addChore("Research", LocalDate.now());

        assertAll(
                () -> assertThrows(DuplicatedChoreException.class,
                        () -> service.editChore("Research", LocalDate.now().plusDays(7), LocalDate.now())),
                () -> assertThrows(DuplicatedChoreException.class,
                        () -> service.editChore("Research", LocalDate.now().plusDays(7), LocalDate.now().plusDays(7)))
                );
    }
    @Test
    @DisplayName("#editChore > Edit chore when the chore does not exist > Throw an exception")
    void editChoreWhenTheChoreDoesNotExistThrowAnException () {
        ChoreService service = new ChoreService();

        assertAll(
                () -> assertThrows(ChoreNotFoundException.class,
                        () -> service.editChore("Chore 1", LocalDate.now().plusDays(1), LocalDate.now())),
                () -> assertThrows(ChoreNotFoundException.class,
                        () -> service.editChore("Chore 1", LocalDate.now().plusDays(1), "New description"))
                );
    }
    @Test
    @DisplayName("#editChore > Edit chore but the chore is already completed > Throw an exception")
    void editChoreThatIsAlreadyCompletedThrowAnException() {
        ChoreService service = new ChoreService();

        service.getChores().add(new Chore("Chore #1", Boolean.TRUE, LocalDate.now()));

        assertAll(
                () -> assertThrows(EditChoreWithCompletedStatusException.class,
                        () -> service.editChore("Chore #1", LocalDate.now(), LocalDate.now().plusDays(1))),
                () -> assertThrows(EditChoreWithCompletedStatusException.class,
                        () -> service.editChore("Chore #1", LocalDate.now(), "New description"))
        );
    }



}
