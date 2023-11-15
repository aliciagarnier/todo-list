package br.edu.unifalmg.repository.impl;

import br.edu.unifalmg.domain.Chore;
import br.edu.unifalmg.repository.ChoreRepository;
import br.edu.unifalmg.repository.book.ChoreBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MySQLChoreRepository implements ChoreRepository {

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;


    @Override
    public List<Chore> load() {
        if (!connectToMySQL()) {
            return new ArrayList<>();
        }
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(ChoreBook.FIND_ALL_CHORES);

            List<Chore> chores = new ArrayList<>();
            while (resultSet.next()) {
                Chore chore = Chore.builder()
                        .id(resultSet.getLong("id"))
                        .description(resultSet.getString("description"))
                        .isCompleted(resultSet.getBoolean("isCompleted"))
                        .deadline(resultSet.getDate("deadline").toLocalDate())
                        .build();

                chores.add(chore);
            }
            return chores;

        } catch (SQLException e) {
            System.out.println("Error when connecting to database");
        } finally {
            closeConnections();
        }
     return null;
    }

    @Override
    public boolean save(Chore chore) {
       if(!connectToMySQL()) {
           return Boolean.FALSE;
       }
       try {
           preparedStatement = connection.prepareStatement(ChoreBook.INSERT_CHORE);
           preparedStatement.setString(1, chore.getDescription());
           preparedStatement.setBoolean(2, chore.getIsCompleted());
           preparedStatement.setDate(3, Date.valueOf(chore.getDeadline()));
           int affectedRows = preparedStatement.executeUpdate();
           if (affectedRows > 0) {
               return Boolean.TRUE;
           }
           return Boolean.FALSE;
       } catch (SQLException e) {
           System.out.println("Error when inserting a new chore on database.");
       } finally {
           closeConnections();
       }
       return Boolean.FALSE;
    }

    public boolean saveAll (List<Chore> chores) {
        return false;
    }

    @Override
    public boolean update(Chore chore) {
        if (!connectToMySQL()) {
            return Boolean.FALSE;
        }
        try {
            preparedStatement = connection.prepareStatement(ChoreBook.UPDATE_CHORE);
            preparedStatement.setString(1, chore.getDescription());
            preparedStatement.setDate(2, Date.valueOf(chore.getDeadline())); // pensando no toggleChore.
            preparedStatement.setBoolean(3, chore.getIsCompleted());
            preparedStatement.setLong(4, chore.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;

        } catch (SQLException e) {
            System.out.println("Error when updating a chore on database");
        } finally {
            closeConnections();
        }
     return Boolean.FALSE;
    }

    private boolean connectToMySQL () {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lifecycle?" + "user=root&password=password");
            return Boolean.TRUE;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error when connecting to database.");
        }
       return Boolean.FALSE;
    }


    public void closeConnections() {
        try {
        if (Objects.nonNull(connection) && !connection.isClosed()) {
            connection.close();
        }

        if (Objects.nonNull(statement) && !statement.isClosed()) {
            statement.close();
        }

        if (Objects.nonNull(preparedStatement) && !preparedStatement.isClosed()) {
            preparedStatement.close();
        }

        if (Objects.nonNull(resultSet) && !resultSet.isClosed()) {
            resultSet.close();
        }
    } catch (SQLException e) {
            System.out.println("Error when closing database connections. ");
        }
    }


}

