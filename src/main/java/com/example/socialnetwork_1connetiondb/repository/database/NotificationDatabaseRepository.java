package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.NotificationDTO;
import com.example.socialnetwork_1connetiondb.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NotificationDatabaseRepository extends AbstractDatabaseRepository<Long, NotificationDTO> {
    public NotificationDatabaseRepository(Validator<NotificationDTO> validator, DataBaseAccess dataBaseAccess) {
        super(validator, dataBaseAccess, "notifications");
        loadData();
    }

    @Override
    protected void loadData() {
        findAll_DB().forEach(super::save);
    }

    @Override
    protected Iterable<NotificationDTO> findAll_DB() {
        Set<NotificationDTO> notifications = new HashSet<>();
        try {
            PreparedStatement statement = dataBaseAccess.createStatement("SELECT * FROM " + tableName + ";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("Id");
                Long sendingUserId = resultSet.getLong("SendingUserId");
                Long receivingUserId = resultSet.getLong("ReceivingUserId");
                String message = resultSet.getString("Message");
                Boolean seenStatus = resultSet.getBoolean("SeenStatus");
                OffsetDateTime date;
                try{
                    date = resultSet.getObject("Date", OffsetDateTime.class);
                } catch (SQLException e) {
                    e.printStackTrace();
                    continue;
                }
                LocalDateTime localDate = date.toLocalDateTime();

                NotificationDTO notificationDTO = new NotificationDTO(sendingUserId, receivingUserId, message, seenStatus, localDate);
                notificationDTO.setId(id);
                notifications.add(notificationDTO);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notifications;
    }

    @Override
    public Optional<NotificationDTO> save(NotificationDTO notificationDTO){
        if (notificationDTO == null){
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(notificationDTO);
        String sql = "INSERT INTO " + tableName + " (\"SendingUserId\", \"ReceivingUserId\", \"Message\", \"SeenStatus\", \"Date\") "
                + "VALUES (?, ?, ?, ?, ?) RETURNING \"Id\";";
        try {
            PreparedStatement statement = dataBaseAccess.createStatement(sql);
            statement.setLong(1, notificationDTO.getSendingUserId());
            statement.setLong(2, notificationDTO.getReceivingUserId());
            statement.setString(3, notificationDTO.getMessage());
            statement.setBoolean(4, notificationDTO.getSeenStatus());
            statement.setObject(5, Timestamp.valueOf(notificationDTO.getDate()));
            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    notificationDTO.setId(resultSet.getLong("Id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(entities.putIfAbsent(notificationDTO.getId(), notificationDTO));
    }

    @Override
    public Optional<NotificationDTO> update(NotificationDTO entity){
        Optional<NotificationDTO> notificationDTO = super.update(entity);
        String sql = "UPDATE " + tableName + " SET \"SendingUserId\" = ?, \"ReceivingUserId\" = ?, \"Message\" = ?, \"SeenStatus\" = ?, \"Date\" = ? WHERE \"Id\" = ?;";
        if (notificationDTO.isEmpty()){
            try {
                PreparedStatement statement = dataBaseAccess.createStatement(sql);
                statement.setLong(1, entity.getSendingUserId());
                statement.setLong(2, entity.getReceivingUserId());
                statement.setString(3, entity.getMessage());
                statement.setBoolean(4, entity.getSeenStatus());
                statement.setObject(5, Timestamp.valueOf(entity.getDate()));
                statement.setObject(6, entity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return notificationDTO;
    }
}
