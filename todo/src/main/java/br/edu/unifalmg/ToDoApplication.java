package br.edu.unifalmg;

import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.impl.FileChoreRepository;
import br.edu.unifalmg.repository.impl.MySQLChoreRepository;
import br.edu.unifalmg.service.ChoreService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDate;

public class ToDoApplication {
       public static void main(String[] args) {
        ChoreRepository repository = new MySQLChoreRepository();
        ChoreService service = new ChoreService(repository);
        System.out.println("Tamanho da lista de chores: " + service.getChores().size());
        service.addChore("Organizar o quarda roupa", LocalDate.now());
        service.addChore("Limpar a caixa de areia dos gatos", LocalDate.now());

        service.loadChores();
        service.printChores();

        service.toggleChore("Limpar a caixa de areia dos gatos", LocalDate.now());
        service.updateChore(service.getChores().get(1));
        service.loadChores();
        service.printChores();


    }


}
