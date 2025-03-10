# Developing a social network Java application with graphical user interface with a simple model from which relevant information can be extracted.
## Implemented feature: CRUD operations, managing friendships, displaying user communities, managing messaging, paginated view, notifications.
## For persistence, the data was stored in a database (PostgreSQL).
## For the graphical user interface, JavaFX and CSS where used.

### A few images of the application to create an overview:
When the app is opened, it first shows a login window:
![login.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Flogin.png)
If the user does not have an account, he can create one by clicking the create a new account button and completing the form:
![create-new-account.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Fcreate-new-account.png)
When the user is logged in, a window opens on the home page, where the user can do some things: see his friends, search/add/remove friends. He can also see received friend requests, notifications. And, last but not least, he can send messages with his friends.
![user-home-page.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Fuser-home-page.png)
How can the user search for people?
![search-new-users.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Fsearch-new-users.png)
And if they click on the person they are looking for, the user profile page is displayed, where they can see the friend list of the user they are looking for and can also add or remove them as a friend.
![searched-user-page.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Fsearched-user-page.png)
If the user clicks on one of their friends in the list, their chat is displayed.
![chat.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Fchat.png)
The user can see the received friend requests by clicking on the friend requests button when they can also accept or delete friend requests:
![friend-requests.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Ffriend-requests.png)
The user can see their notifications:
![notifications.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Fnotifications.png)
And finally, the user can log out:
![logout.png](src%2Fmain%2Fresources%2Fcom%2Fexample%2Fsocialnetwork_1connetiondb%2FreadMeImages%2Flogout.png)