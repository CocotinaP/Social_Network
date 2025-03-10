package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.FriendshipDTO;
import com.example.socialnetwork_1connetiondb.utils.paging.Page;
import com.example.socialnetwork_1connetiondb.utils.paging.Pageable;

public interface FriendRepository extends PagingRepository<Long, FriendshipDTO> {
    Page<FriendshipDTO> findAllOnPage(Long userId, Pageable pageable);
}
