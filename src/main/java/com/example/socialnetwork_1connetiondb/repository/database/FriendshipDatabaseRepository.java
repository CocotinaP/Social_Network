package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.FriendshipDTO;
import com.example.socialnetwork_1connetiondb.domain.validators.FriendshipValidator;
import com.example.socialnetwork_1connetiondb.utils.paging.Page;
import com.example.socialnetwork_1connetiondb.utils.paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FriendshipDatabaseRepository extends AbstractDatabaseRepository<Long, FriendshipDTO> implements FriendRepository{
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSxxxxx");

    public FriendshipDatabaseRepository(FriendshipValidator validator, DataBaseAccess dataBaseAccess){
        super(validator, dataBaseAccess, "\"friendships\"");
        loadData();
    }

    @Override
    protected void loadData() {
        findAll_DB().forEach(super::save);
    }

    @Override
    protected Iterable<FriendshipDTO> findAll_DB() {
        Set<FriendshipDTO> friendships = new HashSet<>();
        try {
            PreparedStatement statement = dataBaseAccess.createStatement("SELECT * FROM " + tableName + ";");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Long id = resultSet.getLong("Id");
                Long idUser1 = resultSet.getLong("IdUser1");
                Long idUser2 = resultSet.getLong("IdUser2");
                OffsetDateTime friendsFrom;
                try {
                    friendsFrom = resultSet.getObject("FriendsFrom", OffsetDateTime.class);
                } catch (Exception e) {
                    System.err.println("Eroare la parsarea datei: " + resultSet.getString("FriendsFrom"));
                    e.printStackTrace();
                    continue;
                } // Conversia OffsetDateTime la LocalDateTime fără fus orar (dacă este necesar)
                LocalDateTime localFriendsFrom = friendsFrom.toLocalDateTime();

                FriendshipDTO friendshipDTO = new FriendshipDTO(idUser1, idUser2, localFriendsFrom);
                friendshipDTO.setId(id);
                friendships.add(friendshipDTO);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships;
    }

    @Override
    public Optional<FriendshipDTO> save(FriendshipDTO friendshipDTO) {
        if (friendshipDTO == null) {
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(friendshipDTO);
        String sql = "INSERT INTO " + tableName + " (\"IdUser1\", \"IdUser2\", \"FriendsFrom\") "
                + "VALUES (?, ?, ?) RETURNING \"Id\";";
        try {
            PreparedStatement statement = dataBaseAccess.createStatement(sql);
            statement.setLong(1, friendshipDTO.getIdUser1());
            statement.setLong(2, friendshipDTO.getIdUser2());
            statement.setObject(3, Timestamp.valueOf(friendshipDTO.getFriendsFrom()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    friendshipDTO.setId(resultSet.getLong("Id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(entities.putIfAbsent(friendshipDTO.getId(), friendshipDTO));
    }


    @Override
    public Optional<FriendshipDTO> update(FriendshipDTO entity) {
        Optional<FriendshipDTO> friendShip = super.update(entity);
        if (friendShip.isPresent() && !friendShip.get().equals(entity)) {
            String sql = "UPDATE " + tableName + " SET \"IdUser1\" = ?, \"IdUser2\" = ?, \"FriendsFrom\" = ? WHERE \"Id\" = ?;";
            try {
                PreparedStatement statement = dataBaseAccess.createStatement(sql);
                statement.setLong(1, entity.getIdUser1());
                statement.setLong(2, entity.getIdUser2());
                statement.setObject(3, Timestamp.valueOf(entity.getFriendsFrom()));
                statement.setLong(4, entity.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return friendShip;
    }

    @Override
    public Page<FriendshipDTO> findAllOnPage(Long userId, Pageable pageable) {
        List<FriendshipDTO> friendships = new ArrayList<>();
        int total = 0;
        int offset = pageable.getPageNumber()* pageable.getPageSize();
        int limit = pageable.getPageSize();
        String sql = "SELECT * FROM " + tableName + "WHERE \"IdUser1\" = ? OR \"IdUser2\" = ? ORDER BY \"FriendsFrom\" LIMIT ? OFFSET ?";
        try {
            PreparedStatement statement = dataBaseAccess.createStatement(sql);
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setInt(3, limit);
            statement.setInt(4, offset);

            try(ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()){
                    Long id = resultSet.getLong("Id");
                    Long idUser1 = resultSet.getLong("IdUser1");
                    Long idUser2 = resultSet.getLong("IdUser2");

                    OffsetDateTime friendsFrom;
                    try {
                        friendsFrom = resultSet.getObject("FriendsFrom", OffsetDateTime.class);
                    } catch (Exception e) {
                        System.err.println("Eroare la parsarea datei: " + resultSet.getString("FriendsFrom"));
                        e.printStackTrace();
                        continue;
                    } // Conversia OffsetDateTime la LocalDateTime fără fus orar (dacă este necesar)
                    LocalDateTime localFriendsFrom = friendsFrom.toLocalDateTime();

                    FriendshipDTO friendshipDTO = new FriendshipDTO(idUser1, idUser2, localFriendsFrom);
                    friendshipDTO.setId(id);
                    friendships.add(friendshipDTO);
                }
            }
            String countSql = "SELECT COUNT(*) FROM " + tableName + " WHERE \"IdUser1\" = ? OR \"IdUser2\" = ?";
            try {
                PreparedStatement countStatement = dataBaseAccess.createStatement(countSql);
                countStatement.setLong(1, userId);
                countStatement.setLong(2, userId);
                try (ResultSet countResultSet = countStatement.executeQuery()){
                    if (countResultSet.next()){
                        total = countResultSet.getInt(1);
                    }
                }
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(friendships, total);
    }
}
