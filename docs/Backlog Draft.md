# **Backlog** 

## Stakeholders
- Application user: Any person that is able to use the client application.
- Application administrator: The person who has access to the server application, which the clients connect to.

## Terminology:
- Board: it is the interface that a user can read and possibly edit
- Card: a single object that has text attributes and can be moved around the board
- List: a collection of cards that belong to a certain category, e.g. TODO or Planning
- Nested task list: tasks that can be checked and unchecked located in a card
- Tag: a category that can be added to a card
- Overview (of a board): the positions and attributes of the cards, lists and tags visible to the user
- Client - the client application which connects to the server application and to which the users have access to 

## Functional Requirements

- ### Basic Features
    - As a user I want to be able to create cards
        - must have an add card button for each list
        - after pressing the button the user must be asked about the card's title
        - should have a delete card button in the pop-up
        - must have an edit button in the pop-up
    - As a user I want to be able to edit cards
        - it should be possible to edit the title of the card from the overview
    - As a user, I want to be able to delete cards
        - it must be possible to delete cards from the overview
    - As a user, I want to move cards
        - it must be possible to move cards between lists 
        - it must be possible to move cards within a list
    - As a user I want to be able to create lists
        - there must be button through which a new list can be created
        - it must be possible to fill out the list name when creating it
    - As a user I want to be able to edit lists
        - it must be possible to rename a list from the overview
    - As a user I want to be able to delete lists
        - it must be possible to delete a list from the overview
    - As a user I want to be able to move lists
        - it must be possible to move lists around the board (before/after another list(s))
    - As a user I want to work on a board with other users
        - it must be possible to work on the same board with other users at the same time
        
- ### Multi-Board
    - As a user I want to be able to create more boards
        - there should be a "Welcome" page with a button to create a board and a button to join a board if the user has no boards saved
        - when a user clicks the "Create a Board" button, a pop-up window requesting the board name must be shown
    - As a user I want to have convenient access to all of my boards
      - any boards that a user creates or joins must be added to their local list of boards
      - a user must then at any point be able to chose a board from that list and switch to it
      - that local list of boards should be persistent for a user even if they restart their client application
    - As an admin I want to be able to delete boards
        - it must not be possible to join that board
        - the code of the board must be invalidated thereafter
   - As a user, I want to be able to collaborate with other users on the same board
        - the overview of the board that I am working on with others must synchronize when someone makes a change
        - the app should not break/crash when two users make a change to the same part of a board at the same time
        - any changes made to a board by any user must be saved to the server so they can be retreived when a user opens that board on their client app
        - those changes must not be lost when the server restarts
    - As a user, I want to share my board with other users and join other users' boards
        - for every board that I have access to there must be a code that I can view, so I can send it to others, in order for them to have access to that board as well
        - when a user enters that board code into their client app, the board must be added to their local list of boards and they must be able to edit any part of the board, or even delete it

- ### Additional Card Details
    - As a user I want to add details to cards through a pop-up so I can my cards contain more information 
        - the pop-up should ask for the card description
        - the pop-up should ask for tags that the card should have
    - As a user I want to be able to view additional card components so I can see the details of the tasks
        - should have a pop-up that displays information about the card
        - the information should contain the card title
        - the information should contain the card description
        - the information should contain the card's tags
        - the information should contain the nested task list
    - As a user I want to edit the cards' details through a pop-up so I can update tasks' details
        - through the edit button in the view card window a new pop-up should appear for editing
        - it should be possible to edit the title of the card
        - it should be possible to edit card details - description, nested lists, tags
    - As a user I want to delete a card through the pop-up so the card does no longer exist on the board
        - it should be possible to delete cards from the view card window
        
- ### Tag Support
    - As a user I want to be able to create tags so I can organise the cards better regardless of the list they are in
        - it should be possible to create a new tag, which is automatically added to a card 
    - As a user I want to be able to add and remove tags from cards so I can improve the organisation of the cards in my board
       - it should be possible to add an existing tag to a card 
       - it should be possible to remove a tag from a card
    - As a user I want to be able to manage tags for the whole board so I can work more efficiently by managing all the tags rather than the singular tags added to cards
       - it could be possible to create and delete a tag in the board settings

- ### Customization (Background, Tag colors)
    - As a user I want to be able to customize boards so I can make them more personalized for my needs
        - it should be possible to customize the board's visual appearance (background color, making the usable keyboard shortcuts visible) through a board settings page or a pop-up
	    - it could be possible to change the board's name through a board settings page or a pop-up

- ### Keyboard shortcuts
    - As a user I want to be able to manage the board using shortcuts, so that my workflow becomes more efficient, by doing tasks more quickly.
        - it should be possible to add cards and lists using shortcuts
        - it could be possible to add tags using shortcuts
        - it could be possible to move cards using shortcuts
        - it could be possible to move or reorder the lists of the board using shortcuts
        - it could be possible to create and join boards using shortcuts
        - it could be possible to open a board's settings using shortcuts
        - other shortcuts could be included, potentially matching to all functionalities
- ### Protected Boards
    - As a user, I should be able to determine what kind of access to give to other users when I share a board with them to ensure the safety of the data in each board
        - there should be a code(or a password) that gives "read-only" access to the board, which enables users to view any area of the board but prevents them from making changes.
        - if a user has entered a "read-only" code for a board they should be able to share it with others, but they should not be able to access the read/write code for that board
        - if a user has read/write permission for a board they should be able to access (and thus be able to share) both the read code and the read/write code for that particular board
    - As a user, I should be able to upgrade my privileges for a board by entering another code in order to keep my boards ordered and in good condition
        - if a user has "read-only" privileges to a board and they enter the read/write code for that board, they should automatically be "upgraded" to have read/write privileges to that board (with the board showing only once in the board list)
- ### Manage Server
    - As an administrator, I want to have an application which I can access from anywhere in order to manage the server when I need to, without being physically there.
        - it should be possible to manage the server remotely, i.e., from the client side
        - admin authentication should be enforced, so that normal users cannot manage the server
        - a GUI should be used rather than a CLI, so that it is more accessible and intuitive
    - As an administrator, I want to be able to restart the server if I want to alter configurations or if some error happened
        - it should be possible to start the server
        - it should be possible to stop the server
        - it should be possible to restart the server
    - As an administrator, I want to be able to manage any board from the server remotely, so that I can apply changes requested by normal users
        - it should be possible to visualise all boards and their codes
        - it should be possible to restrict a board, making it write-protected
        - it should be possible to open a board, removing its protection
        - it should be possible to change the password of a protected board
        - it could be possible to change the board's name, assuming it is not an auto-generated hash
        - it could be possible to add new boards
        - it could be possible to delete boards

## Non-functional Requirements

### Product
- A database must be used to store the server's data
- The client must connect to the server in order to access a board
- The server must be able to server multiple clients simultaneously
- Updates to the overview should appear in less than three seconds
- The application should run in an environment with at least 2GB of available RAM
- The user interface must be easy to navigate and understand

### Organisational
- Java SE 19 should be used
- JavaFX and Spring Boot must be used
- Libraries with critical vulnerabilities must not be used
- Interfaces must be documented using JavaDoc

### External
- The application should run successfully and meet all requirements regardless of operating system and architecture
- A license can be used
