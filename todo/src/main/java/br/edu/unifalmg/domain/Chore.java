package br.edu.unifalmg.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chore {

    private String description;

    private Boolean isCompleted;

    private LocalDate deadline;

    @Override
    public String toString () {
        String choreStatus = "Not completed";

           if (isCompleted)
           {
               choreStatus = "Completed";
           }

        return "Description: " + description +  " Deadline: " + deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " Status: " + choreStatus;
    }

}
