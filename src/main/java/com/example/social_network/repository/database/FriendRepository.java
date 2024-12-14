package com.example.social_network.repository.database;

import com.example.social_network.domain.FriendshipDTO;
import com.example.social_network.utils.paging.Page;
import com.example.social_network.utils.paging.Pageable;

public interface FriendRepository extends PagingRepository<Long, FriendshipDTO> {
    Page<FriendshipDTO> findAllOnPage(Long userId, Pageable pageable);
}
