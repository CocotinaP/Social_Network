package com.example.socialnetwork_1connetiondb.repository.file;


import com.example.socialnetwork_1connetiondb.domain.FriendshipDTO;
import com.example.socialnetwork_1connetiondb.domain.validators.Validator;

import java.time.LocalDateTime;

/**
 * CRUD operations friendship file repository
 */
public class FriendshipRepository extends AbstractFileRepository<Long, FriendshipDTO> {

    public FriendshipRepository(Validator<FriendshipDTO> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public FriendshipDTO lineToEntity(String line) {
        String[] parts = line.split(";");
        FriendshipDTO prietenie = new FriendshipDTO(Long.parseLong(parts[1]), Long.parseLong(parts[2]), LocalDateTime.parse(parts[3], FriendshipDTO.dateTimeFormat));
        prietenie.setId(Long.parseLong(parts[0]));
        return prietenie;
    }

    @Override
    public String entityToLine(FriendshipDTO entity) {
        return entity.getId() + ";" + entity.getIdUser1() + ";" + entity.getIdUser2()
                + ";" + entity.getFriendsFrom().format(FriendshipDTO.dateTimeFormat);
    }
}
