package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.MessageDTO;
import com.example.socialnetwork_1connetiondb.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MessageDatabaseRepository extends AbstractDatabaseRepository<Long, MessageDTO> {
    public MessageDatabaseRepository(Validator<MessageDTO> validator, DataBaseAccess dataBaseAccess) {
        super(validator, dataBaseAccess, "messages");
        loadData();
    }

    @Override
    protected void loadData() {
        findAll_DB().forEach(super::save);
    }

    @Override
    protected Iterable<MessageDTO> findAll_DB() {
        Set<MessageDTO> messagesDTO = new HashSet<>();
        try {
            PreparedStatement statement = dataBaseAccess.createStatement("SELECT * FROM " + tableName + ";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("Id");
                Long from = resultSet.getLong("From");
                Long to = resultSet.getLong("To");
                String message = resultSet.getString("Message");
                Long reply = resultSet.getLong("Reply");
                OffsetDateTime date;
                try{
                    date = resultSet.getObject("Date", OffsetDateTime.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                    continue;
                }
                LocalDateTime localDate = date.toLocalDateTime();

                MessageDTO messageDTO = new MessageDTO(from, to, message, localDate, reply);
                messageDTO.setId(id);
                messagesDTO.add(messageDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messagesDTO;
    }

    @Override
    public Optional<MessageDTO> save(MessageDTO messageDTO){
        if (messageDTO == null){
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(messageDTO);
        String sql = "INSERT INTO " + tableName + " (\"From\", \"To\", \"Message\", \"Date\", \"Reply\") "
                + "VALUES (?, ?, ?, ?, ?) RETURNING \"Id\";";
        try {
            PreparedStatement statement = dataBaseAccess.createStatement(sql);
            statement.setLong(1, messageDTO.getFromId());
            statement.setLong(2, messageDTO.getToId());
            statement.setString(3, messageDTO.getMessage());
            statement.setObject(4, Timestamp.valueOf(messageDTO.getDate()));
            statement.setLong(5, messageDTO.getReplyId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    messageDTO.setId(resultSet.getLong("Id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(entities.putIfAbsent(messageDTO.getId(), messageDTO));
    }

    @Override
    public Optional<MessageDTO> update(MessageDTO entity){
        Optional<MessageDTO> messageDTO = super.update(entity);
        if (messageDTO.isEmpty()){
            String sql = "UPDATE " + tableName + " SET \"From\" = ?, \"To\" = ?, \"Message\" = ?, \"Date\" = ?, \"Reply\" = ? WHERE \"Id\" = ?;";
            try {
                PreparedStatement statement = dataBaseAccess.createStatement(sql);
                statement.setLong(1, entity.getFromId());
                statement.setLong(2, entity.getToId());
                statement.setString(3, entity.getMessage());
                statement.setObject(4, Timestamp.valueOf(entity.getDate()));
                statement.setLong(5, entity.getReplyId());
                statement.setObject(6, entity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return messageDTO;
    }

}
