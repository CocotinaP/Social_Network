package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.UserValidator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDataBaseRepository extends AbstractDatabaseRepository<Long, User> {
    public UserDataBaseRepository(UserValidator validator, DataBaseAccess dataBaseAccess) {
        super(validator, dataBaseAccess, "users");
        loadData();
    }

    @Override
    protected void loadData() {
        findAll_DB().forEach(super::save);
    }

    @Override
    protected Iterable<User> findAll_DB() {
        Set<User> users = new HashSet<>();
        try {
            PreparedStatement statement = dataBaseAccess.createStatement("SELECT * FROM " + tableName + ";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("Id");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String userName = resultSet.getString("UserName");
                String password = resultSet.getString("Password");

                User user = new User(firstName, lastName, userName, password);
                user.setId(id);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public Optional<User> save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(user);
        String sql = "INSERT INTO " + tableName + " (\"FirstName\", \"LastName\", \"UserName\", \"Password\") "
                + "VALUES (?, ?, ?, ?) RETURNING \"Id\";";
        try {
            PreparedStatement statement = dataBaseAccess.createStatement(sql);
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUserName());
            statement.setString(4, user.getPassword());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("Id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(entities.putIfAbsent(user.getId(), user));
    }


    @Override
    public Optional<User> update(User entity) {
        Optional<User> user = super.update(entity);

        if (user.isEmpty()) {
            String sql = "UPDATE " + tableName + " SET \"FirstName\" = ?, \"LastName\" = ?, \"UserName\" = ?, \"Password\" = ? WHERE \"Id\" = ?;";
            try {
                PreparedStatement statement = dataBaseAccess.createStatement(sql);
                statement.setString(1, entity.getFirstName());
                statement.setString(2, entity.getLastName());
                statement.setString(3, entity.getUserName());
                statement.setString(4, entity.getPassword());
                statement.setLong(5, entity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return user;
    }
}