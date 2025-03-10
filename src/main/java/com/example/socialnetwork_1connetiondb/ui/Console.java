package com.example.socialnetwork_1connetiondb.ui;

import com.example.socialnetwork_1connetiondb.domain.Friendship;
import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.service.Service;
import com.example.socialnetwork_1connetiondb.service.ServiceException;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Console {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";

    private Service service;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructor.
     * @param service - service
     */
    public Console(Service service) {
        this.service = service;
    }

    /**
     * Print the menu.
     */
    private void menu(){
        System.out.println(BLUE + "Menu:" + RESET);
        System.out.println(GREEN + "1" + RESET + "." + BLUE + "Adauga utilizator;" + RESET);
        System.out.println(GREEN + "2" + RESET + "." + BLUE + "Sterge utilizator;" + RESET);
        System.out.println(GREEN + "3"  + RESET + "." + BLUE + "Afiseaza toti utilizatorii;" + RESET);
        System.out.println(GREEN + "4" + RESET + "." + BLUE + "Adauga prietenie;" + RESET);
        System.out.println(GREEN + "5" + RESET + "." + BLUE + "Sterge prietenie;" + RESET);
        System.out.println(GREEN + "6" + RESET + "." + BLUE + "Afiseaza toate prieteniile;" + RESET);
        System.out.println(GREEN + "7" + RESET + "." + BLUE + "Afiseaza comunitaile;" + RESET);
        System.out.println(GREEN + "8" + RESET + "." + BLUE + "Afiseaza cea mai sociabila componeta;" + RESET);
        System.out.println(GREEN + "9" + RESET + "." + BLUE + "Cauta user dupa nume;" + RESET);
        System.out.println(GREEN + "10" + RESET + "." + BLUE + "Trimite cerere de prietenie;" + RESET);
        System.out.println(GREEN + "11" + RESET + "." + BLUE + "Sterge cerere de prietenie;" + RESET);
        System.out.println(GREEN + "12" + RESET + "." + BLUE + "Accepta cerere de prietenie;" + RESET);
        System.out.println(GREEN + "13" + RESET + "." + BLUE + "Afieseaza toate cererile de prietenie;" + RESET);
        System.out.println(GREEN + "14" + RESET + "." + BLUE + "Trimite mesaj;" + RESET);
        System.out.println(GREEN + "15" + RESET + "." + BLUE + "Afieseaza toate mesajele;" + RESET);
        System.out.println(GREEN + "0" + RESET + "." + BLUE + "Inchide." + RESET);
    }

    /**
     * Save user.
     */
    private void saveUser(){
        System.out.println("First name: ");
        String firstName = scanner.nextLine();
        System.out.println("Last name: ");
        String lastName = scanner.nextLine();
        System.out.println("UserName: ");
        String userName = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();
        try {
            service.saveUser(firstName, lastName, userName, password);
        } catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e)
        {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    /**
     * Delete an user.
     */
    private void deleteUser(){
        System.out.println("Id: ");
        String id = scanner.nextLine();
        try{
            service.deleteUser(Long.parseLong(id));
        } catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    /**
     * Print an user.
     * @param user to be printed.
     */
    private void printUser(User user){
        System.out.println(PURPLE + user + RESET);
    }

    /**
     * Print all users.
     */
    private void showAllUsers(){
        Iterable<User> users = service.findAllUsers();
        if (!users.iterator().hasNext()) {
            System.out.println(PURPLE + "Nu exista utilizatori!" + RESET);
        }

        //users.forEach(System.out::println);
        users.forEach(this::printUser);
    }

    /**
     * Save a friendship.
     */
    private void saveFriendship(){
        System.out.println("Id1: ");
        String id1 = scanner.nextLine();
        System.out.println("Id2: ");
        String id2 = scanner.nextLine();
        try{
            service.saveFriendship(Long.parseLong(id1), Long.parseLong(id2));
        } catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch (ServiceException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    /**
     * Delete a friendship.
     */
    private void deleteFriendship(){
        System.out.println("Id: ");
        String id = scanner.nextLine();
        try{
            service.deleteFriendship(Long.parseLong(id));
        } catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch (ServiceException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    /**
     * Print all friendships.
     */
    private void showAllFriendships(){
        Iterable<Friendship> friendships = service.findAllFriendships();
        if (!friendships.iterator().hasNext()){
            System.out.println(PURPLE + "Nu exista prietenii!" + RESET);
        }

        friendships.forEach(friendship -> {
            System.out.println(PURPLE + friendship + RESET);
        });
    }

    /**
     * Print the number of coummunities and all coummunties.
     */
    private void communities(){
        List<List<User>> communities = service.communities();
        System.out.println("Nr. de comunitati= " + PURPLE + communities.size() + RESET);
        System.out.println("Comunitatile sunt: ");

        communities.forEach(community->{
            StringBuilder stringCommunity = new StringBuilder();
            community.forEach(user -> {
                stringCommunity.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
            });
            stringCommunity.setLength(stringCommunity.length() - 2);
            if (community.size() == 1){
                stringCommunity.append("\uD83D\uDE15");
            }
            else{
                stringCommunity.append("\uD83E\uDD70");
            }
            System.out.println(PURPLE + stringCommunity.toString() + RESET);
        });
    }

    /**
     * Print the most sociable communities.
     */
    private  void theMostSociableCommunity(){
        List<List<User>> communities = service.theMostSociableCommunity();
        communities.forEach(community->{
            StringBuilder stringCommunity = new StringBuilder();
            community.forEach(user -> {
                stringCommunity.append(user.getFirstName()).append(" ").append(user.getLastName()).append(", ");
            });
            stringCommunity.setLength(stringCommunity.length() - 2);
            System.out.println(PURPLE + stringCommunity.toString() + RESET);
        });
    }

    /**
     * Find an user by name.
     */
    private void findOneUserByName(){
        System.out.println("First name: ");
        String firstName = scanner.nextLine();
        System.out.println("Last name: ");
        String lastName = scanner.nextLine();
        Optional<User> user = service.findOneUserByName(firstName + ' ' + lastName);
        if (user.isPresent()) {
            System.out.println(PURPLE + user.get() + RESET);
        }
        else{
            System.out.println(RED + "User inexistent!\n" + RESET);
        }
    }

    public void saveFriendRequest(){
        System.out.println("ID of the sending user: ");
        String sendingUserId = scanner.nextLine();
        System.out.println("ID of the receiving user: ");
        String receivingUserId = scanner.nextLine();
        try{
            service.saveFriendRequest(Long.parseLong(sendingUserId), Long.parseLong(receivingUserId));
        }catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch (ServiceException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    public void deleteFriendRequest(){
        System.out.println("Id: ");
        String id = scanner.nextLine();
        try{
            service.deleteFriendRequest(Long.parseLong(id));
        }catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch (ServiceException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    public void acceptFriendRequest(){
        System.out.println("Id: ");
        String id = scanner.nextLine();
        try{
            service.acceptFriendRequest(Long.parseLong(id));
        }catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch (ServiceException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    public void showAllFriendRequests(){
        service.findAllFriendRequests().forEach(System.out::println);
    }

    public void sendMessage(){
        System.out.println("FromId: ");
        String fromId = scanner.nextLine();
        System.out.println("ToId: ");
        String toId = scanner.nextLine();
        System.out.println("Message: ");
        String message = scanner.nextLine();
        System.out.println("ReplyId: ");
        String replyId = scanner.nextLine();
        try{
            if (replyId.isEmpty()){
                service.saveMessage(Long.parseLong(fromId), Long.parseLong(toId), message);
            }
            else{
                service.saveMessage(Long.parseLong(fromId), Long.parseLong(toId), message, Long.parseLong(replyId));
            }
        }catch(IllegalArgumentException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch(ValidationException e) {
            System.out.println(RED + e.getMessage() + RESET);
        } catch (ServiceException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    public void showAllMessages(){
        service.findAllMessages().forEach(System.out::println);
    }

    /**
     * Start the program.
     */
    public void show(){
        String command;
        boolean finish = false;
        while (!finish) {
            menu();
            System.out.println(GREEN + "Comanda:" + RESET);
            command = scanner.nextLine();
            switch (command) {
                case "0":
                    finish = true;
                    break;
                case "1":
                    saveUser();
                    break;
                case "2":
                    deleteUser();
                    break;
                case "3":
                    showAllUsers();
                    break;
                case "4":
                    saveFriendship();
                    break;
                case "5":
                    deleteFriendship();
                    break;
                case "6":
                    showAllFriendships();
                    break;
                case "7":
                    communities();
                    break;
                case "8":
                    theMostSociableCommunity();
                    break;
                case "9":
                    findOneUserByName();
                    break;
                case "10":
                    saveFriendRequest();
                    break;
                case "11":
                    deleteFriendRequest();
                    break;
                case "12":
                    acceptFriendRequest();;
                    break;
                case "13":
                    showAllFriendRequests();
                    break;
                case "14":
                    sendMessage();
                    break;
                case "15":
                    showAllMessages();
                    break;
                default:
                    System.out.println("Comanda invalida!");
            }
        }
    }
}
