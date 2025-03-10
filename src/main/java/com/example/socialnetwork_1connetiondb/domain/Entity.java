package com.example.socialnetwork_1connetiondb.domain;

public class Entity<ID> {
    private ID id;

    /**
     * Set the entity id.
     * @param id - the new entity id.
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * @return the entity id.
     */
    public ID getId() {
        return id;
    }
}
