package br.edu.unifalmg.repository.book;

public class ChoreBook {

    public static final String FIND_ALL_CHORES = "SELECT * FROM lifecycle.chores";
    public static final String INSERT_CHORE = "INSERT INTO lifecycle.chores (`description`, `isCompleted`, `deadline`) VALUES (?,?,?)";
    public static final String UPDATE_CHORE =  "UPDATE lifecycle.chores SET" + "`description` = ?, `deadline` = ?, `isCompleted` = ? WHERE chores.id = ?";



}
