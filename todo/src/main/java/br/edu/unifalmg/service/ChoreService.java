package br.edu.unifalmg.service;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.enumerator.ChoreFilter;
import br.edu.unifalmg.exception.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.edu.unifalmg.repository.ChoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class ChoreService  {
    private List<Chore> chores;

    private ObjectMapper objectMapper;

    private ChoreRepository repository;

    public ChoreService(ChoreRepository choreRepository) {

        chores = new ArrayList<>();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.repository = choreRepository;
    }

    public ChoreService () {}

    /**
     * Method to add a new chore
     *
     * @param description The description of the chore
     * @param deadline The deadline to fulfill the chore
     * @return Chore The new (and uncompleted) chore
     * @throws InvalidDescriptionException When the description is null or empty
     * @throws InvalidDeadlineException When the deadline is null or empty
     * @throws DuplicatedChoreException When the given chore already exists
     */
    public Chore addChore(String description, LocalDate deadline) {
        if (Objects.isNull(description) || description.isEmpty()) {
            throw new InvalidDescriptionException("The description cannot be null or empty");
        }
        if (Objects.isNull(deadline) || deadline.isBefore(LocalDate.now())) {
            throw new InvalidDeadlineException("The deadline cannot be null or before the current date");
        }
        for (Chore chore : chores) {
            if (chore.getDescription().equals(description)
                    && chore.getDeadline().isEqual(deadline)) {
                throw new DuplicatedChoreException("The given chore already exists.");
            }
        }

//         Using anyMatch solution
//
//        boolean doesTheChoreExist = chores.stream().anyMatch(chore -> chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline));
//        if (doesTheChoreExist) {
//            throw new DuplicatedChoreException("The given chore already exists.");
//        }

        // Using Constructor with all arguments
        Chore chore = new Chore(description, Boolean.FALSE, deadline);


//         Using Lombok's builder
//
//         Chore chore = Chore.builder()
//                .description(description)
//                .deadline(deadline)
//                .isCompleted(false)
//                .build();

//         Using Getter and Setters
//
//         Chore chore = new Chore();
//         chore.setDescription(description);
//         chore.setDeadline(deadline);
//         chore.setIsCompleted(Boolean.FALSE);


        chores.add(chore);
        return chore;
    }

    /**
     * Get the added chores.
     *
     * @return List<Chore> The chores added until now.
     */
    public List<Chore> getChores() {
        return this.chores;
    }

    /**
     * Method to delete a given chore.
     *
     * @param description The description of the chore
     * @param deadline The deadline of the chore
     */
    public void deleteChore(String description, LocalDate deadline) {
        if (isChoreListEmpty.test(this.chores)) {
            throw new EmptyChoreListException("Unable to remove a chore from an empty list");
        }
        boolean isChoreExist = this.chores.stream().anyMatch((chore -> chore.getDescription().equals(description)
            && chore.getDeadline().isEqual(deadline)));
        if (!isChoreExist) {
            throw new ChoreNotFoundException("The given chore does not exist.");
        }

        this.chores = this.chores.stream().filter(chore -> !chore.getDescription().equals(description)
                && !chore.getDeadline().isEqual(deadline)).collect(Collectors.toList());
    }

    /**
     *
     * Method to toggle a chore from completed to uncompleted and vice-versa.
     *
     * @param description The chore's description
     * @param deadline The deadline to complete the chore
     * @throws ChoreNotFoundException When the chore is not found on the list
     */
    public void toggleChore(String description, LocalDate deadline) {
        if (!isChoreExist(description, deadline)) {
            throw new ChoreNotFoundException("Chore not found. Impossible to toggle!");
        }

        this.chores = this.chores.stream().map(chore -> {
            if (!chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline)) {
                return chore;
            }
            if (chore.getDeadline().isBefore(LocalDate.now())
                    && chore.getIsCompleted()) {
                throw new ToggleChoreWithInvalidDeadlineException("Unable to toggle a completed chore with a past deadline");
            }
            chore.setIsCompleted(!chore.getIsCompleted());
            return chore;
        }).collect(Collectors.toList());
    }

    public List<Chore> filterChores(ChoreFilter filter) {
        switch (filter) {
            case COMPLETED:
                return this.chores.stream().filter(Chore::getIsCompleted).collect(Collectors.toList());
            case UNCOMPLETED:
                return this.chores.stream().filter(chore -> !chore.getIsCompleted()).collect(Collectors.toList());
            case ALL:
            default:
                return this.chores;
        }
    }

    public String printChores () {
        if (isChoreListEmpty.test(getChores()))
        {
            throw new EmptyChoreListException("No chores at the moment");
        }
        String allChores = getChores().stream().map(Chore::toString).collect(Collectors.joining("\n"));
        System.out.println(allChores);
        return allChores;
    }

    //
    public void editChore (String description, LocalDate deadline, String newDescription) {

          if (Objects.isNull(newDescription) || newDescription.isEmpty())
          {
             throw new InvalidDescriptionException("The description cannot be null or empty");
          }

          if (!isChoreExist(description, deadline))
          {
               throw new ChoreNotFoundException("Unable to edit a chore description that does not exist");
          }

          if (isChoreExist(newDescription, deadline))
          {
            throw new DuplicatedChoreException("The chore already exists");
          }

        chores = getChores().stream().map(chore -> {
                    if (!chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline))
                    {
                        return chore;
                    }

                    if(chore.getIsCompleted())
                    {
                        throw new EditChoreWithCompletedStatusException("Unable to edit chore that is already completed");
                    }

                    chore.setDescription(newDescription);
                    return chore;
                }
        ).collect(Collectors.toList());
    }
    public void editChore (String description, LocalDate deadline, LocalDate newDeadline) {

        if (Objects.isNull(newDeadline) || newDeadline.isBefore(LocalDate.now()))
        {
            throw new InvalidDeadlineException("The deadline cannot be null or before the current date");
        }

        if (!isChoreExist(description, deadline))
        {
            throw new ChoreNotFoundException("Unable to edit a chore deadline that does not exist");
        }

        if (isChoreExist(description, newDeadline))
        {
            throw new DuplicatedChoreException("The chore already exists");
        }

        chores = getChores().stream().map(chore -> {
            if (!chore.getDescription().equals(description) && !chore.getDeadline().isEqual(deadline))
            {
                return chore;
            }

            if(chore.getIsCompleted())
            {
                throw new EditChoreWithCompletedStatusException("Unable to edit chore that is already completed");
            }

            chore.setDeadline(newDeadline);
            return chore;

           }
        ).collect(Collectors.toList());
    }

    /**
     * Load the chores from the repository.
     * The repository can return NULL if no chores are found.
     */
    public void loadChores() {
        this.chores = repository.load();
    }

    /**
     * Save the chores into the file
     *
     * @return TRUE, if the saved was completed and <br/>
     *         FALSE, when the save fails
     */
    public Boolean saveChores() {
        return repository.save(this.chores);
    }

    private final Predicate<List<Chore>> isChoreListEmpty = List::isEmpty;
    private boolean isChoreExist (String description, LocalDate deadline) {
       return getChores().stream().anyMatch((chore) -> chore.getDescription().equals(description) && chore.getDeadline().isEqual(deadline));
    }





}
