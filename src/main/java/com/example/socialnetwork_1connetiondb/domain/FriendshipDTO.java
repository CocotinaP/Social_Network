package com.example.socialnetwork_1connetiondb.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FriendshipDTO extends Entity<Long>{
    private Long idUser1;
    private Long idUser2;
    private LocalDateTime friendsFrom;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MMMM/yy HH:mm");

    /**
     * Constructor.
     * @param user1 - the id of the first friend.
     * @param user2 - the id of the second friend.
     */
    public FriendshipDTO(Long user1, Long user2, LocalDateTime friendsFrom) {
        this.idUser1 = user1;
        this.idUser2 = user2;
        this.friendsFrom = friendsFrom;
    }

    /**
     *
     * @return the id of the first friend.
     */
    public Long getIdUser1() {
        return idUser1;
    }

    /**
     *
     * @return the id of the second friend.
     */
    public Long getIdUser2() {
        return idUser2;
    }

    /**
     *
     * @return the date the friendship was formed
     */
    public LocalDateTime getFriendsFrom(){
        return friendsFrom;
    }

    /**
     * Modify the first friend`s id.
     * @param idUser1 - the new id of the first friend.
     */
    public void setIdUser1(Long idUser1) {
        this.idUser1 = idUser1;
    }

    /**
     * Modify the second friend`s id.
     * @param idUser2 -the new id of the second friend.
     */
    public void setIdUser2(Long idUser2) {
        this.idUser2 = idUser2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUser1(), getIdUser2());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }
        FriendshipDTO other = (FriendshipDTO) obj;
        return getIdUser1().equals(other.getIdUser1()) && getIdUser2().equals(other.getIdUser2());
    }
}
