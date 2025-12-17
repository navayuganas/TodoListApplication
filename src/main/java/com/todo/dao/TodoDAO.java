package main.java.com.todo.dao;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import javax.xml.crypto.Data;

import main.java.com.todo.model.Todo;
import main.java.com.todo.utils.DatabaseConnection;
public class TodoDAO {
    private static final String GET_ALL_TODOS = "SELECT * FROM todos";
    private static final String ADD_TODO = "INSERT INTO todos(title, description, completed, created_at, updated_at) VALUES(?, ?, ?, ?, ?)";
    private static final String UPDATE_TODO = "UPDATE todos SET title=?, description=?, completed=?, updated_at=? WHERE id=?"; 
    public List<Todo> getAllTodos() throws SQLException{
        List<Todo> todos = new ArrayList<>();
        try(Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_ALL_TODOS);
        )
        {
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                Todo todo = new Todo();

                todo.setId(rs.getInt("id"));
                todo.setTitle(rs.getString("title"));
                todo.setDescription(rs.getString("description"));
                todo.setCompleted(rs.getBoolean("completed"));
                todo.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                todo.setUpdated_at(rs.getTimestamp("updated_at").toLocalDateTime());

                todos.add(todo);
            }
        }
        return todos;
    }

    public void addTodo(Todo todo) throws SQLException{
        String title = todo.getTitle();
        String description = todo.getDescription();
        boolean completed = todo.isCompleted();
        LocalDateTime created_at = todo.getCreated_at();
        LocalDateTime updated_at = todo.getUpdated_at();

        try(
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(ADD_TODO, Statement.RETURN_GENERATED_KEYS);
        )
        {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setBoolean(3, completed);
            stmt.setObject(4, created_at);
            stmt.setObject(5, updated_at);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected == 0)
            {
                throw new SQLException("Adding todo failed, no rows affected.");
            }

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if(generatedKeys.next())
            {
                todo.setId(generatedKeys.getInt(1));
            }
            else{
                throw new SQLException("Adding todo failed, no ID obtained.");
            }
        }
        
    }

    public boolean updateTodo(Todo todo) throws SQLException{
        int id=todo.getId();
        String title=todo.getTitle();
        String description=todo.getDescription();
        boolean completed=todo.isCompleted();
        LocalDateTime updated_At=todo.getUpdated_at();

        try(
            Connection conn=DatabaseConnection.getDBConnection();
            PreparedStatement stmt=conn.prepareStatement(UPDATE_TODO);
        )
        {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setBoolean(3, completed);
            stmt.setObject(4, updated_At);
            stmt.setInt(5, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
        

    }
    
}
