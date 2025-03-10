package com.example.socialnetwork_1connetiondb.repository.database;
import com.example.socialnetwork_1connetiondb.domain.Entity;
import com.example.socialnetwork_1connetiondb.utils.paging.Page;
import com.example.socialnetwork_1connetiondb.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> {
    Page<E> findAllOnPage(ID id, Pageable pageable);
}
