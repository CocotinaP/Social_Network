package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.FriendRequestDTO;
import com.example.socialnetwork_1connetiondb.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendRequestDatabaseRepository extends AbstractDatabaseRepository<Long, FriendRequestDTO> {
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxxxxx");

    public FriendRequestDatabaseRepository(Validator<FriendRequestDTO> validator, DataBaseAccess dataBaseAccess) {
        super(validator, dataBaseAccess, "\"friendRequests\"");
        loadData();
    }

    @Override
    protected void loadData() {
        findAll_DB().forEach(super::save);
    }

    @Override
    protected Iterable<FriendRequestDTO> findAll_DB() {
        Set<FriendRequestDTO> friendRequests = new HashSet<>();
        try {
            PreparedStatement statement = dataBaseAccess.createStatement("SELECT * FROM" + tableName + ";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("Id");
                Long sendingUserId = resultSet.getLong("SendingFriendId");
                Long receivingUserId = resultSet.getLong("ReceivingFriendId");
                String  status = resultSet.getString("Status");
                OffsetDateTime submissionDate;
                try{
                    submissionDate = resultSet.getObject("SubmissionDate", OffsetDateTime.class);
                } catch (Exception e){
                    System.err.println("Eroare la parsrea datei: " + resultSet.getString("SubmissionDate"));
                    e.printStackTrace();
                    continue;
                }
                LocalDateTime localSubmissionDate = submissionDate.toLocalDateTime();

                FriendRequestDTO friendRequest = new FriendRequestDTO(sendingUserId, receivingUserId, status, localSubmissionDate);
                friendRequest.setId(id);
                friendRequests.add(friendRequest);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendRequests;
    }

    @Override
    public Optional<FriendRequestDTO> save(FriendRequestDTO friendRequestDTO){
        if (friendRequestDTO == null){
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(friendRequestDTO);
        String sql = "INSERT INTO " + tableName + " (\"SendingFriendId\", \"ReceivingFriendId\", \"SubmissionDate\", \"Status\") "
                + "VALUES (?, ?, ?, ?) RETURNING \"Id\";";
        try {
            PreparedStatement statement = dataBaseAccess.createStatement(sql);
            statement.setLong(1, friendRequestDTO.getSendingUserId());
            statement.setLong(2, friendRequestDTO.getReceivingUserId());
            statement.setObject(3, Timestamp.valueOf(friendRequestDTO.getSubmissionDate()));
            statement.setString(4, friendRequestDTO.getStatus());
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    friendRequestDTO.setId(resultSet.getLong("Id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(entities.putIfAbsent(friendRequestDTO.getId(), friendRequestDTO));
    }

    @Override
    public Optional<FriendRequestDTO> update(FriendRequestDTO entity){
        Optional<FriendRequestDTO> friendRequestDTO = super.update(entity);
        if (friendRequestDTO.isEmpty()){
            String sql = "UPDATE " + tableName + " SET \"SendingFriendId\" = ?, \"ReceivingFriendId\" = ?, \"SubmissionDate\" = ?, \"Status\" = ? WHERE \"Id\" = ?;";
            try {
                PreparedStatement statement = dataBaseAccess.createStatement(sql);
                statement.setLong(1, entity.getSendingUserId());
                statement.setLong(2, entity.getReceivingUserId());
                statement.setObject(3, Timestamp.valueOf(entity.getSubmissionDate()));
                statement.setString(4, entity.getStatus());
                statement.setLong(5, entity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return friendRequestDTO;
    }
}
