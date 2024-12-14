package com.example.social_network.repository.database;
import com.example.social_network.domain.Entity;
import com.example.social_network.utils.paging.Page;
import com.example.social_network.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> {
    Page<E> findAllOnPage(ID id, Pageable pageable);
}
