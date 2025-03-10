package com.example.socialnetwork_1connetiondb.domain;

import java.util.ArrayList;
import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private ArrayList<User> friends;

    /**
     * Constructor.
     * @param firstName of user
     * @param lastName of user
     * @param userName of user
     * @param password of user
     */
    public User(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        friends = new ArrayList<>();
    }

    /**
     *
     * @return user`s firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @return user`s lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @return user's user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @return user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return friends - the list of friends
     */
    public ArrayList<User> getFriends() {
        return friends;
    }

    /**
     * Change the user`s first name.
     * @param firstName the user`s new firstname
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Change the user`s last name.
     * @param lastName - the user`s new lastname
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Change the user's userName.
     * @param userName - the user's userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Change the user's password.
     * @param password - the user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Modify the user`s friend list.
     * @param friends the user`s new friend list
     */
    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        String str = "User{" +
                "ID='" + getId() + '\'' +
                "| FirstName='" + firstName + '\'' +
                "| LastName='" + lastName + '\'' +
                "| UserName='" + userName +'\'' +
                "| Password='" + password +'\'' +
                "| Friends= ";
        if (!friends.isEmpty()) {
            for (User utilizator : friends) {
                str = str + '\'' + utilizator.getFirstName() + " " + utilizator.getLastName() + "', " ;
            }
            str = str.substring(0, str.length()-2);
        }
        else{
            str += "Nu are prieteni... \uD83D\uDE1E";
        }
        str += "}";
        return str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) && getLastName().equals(that.getLastName())
                && getUserName().equals(that.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getUserName());
    }
}
