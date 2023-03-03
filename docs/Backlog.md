# **Backlog** 

## Stakeholders
- Application user - is any person that is able to use the client application
- Application administrator - is the person who has access to the server application, which the clients connect to
- Developers - are students of the University and members of the OOP Project team who contribute to the project and are graded based on their work
- TA - is the SCRUM master, gives advice to the developers through weekly meetings and asseses their teamwork
- Lecturer - is the Client and Project Owner, provides instructions, gives the requirements for the product and asseses the final product of the team
- The University - hosts the GitLab repositories and provides the facilities and staff needed for the course

## Terminology:
- Card - a single object that has text attributes and can be moved around the board
- List - a collection of cards that belong to a certain category, e.g. TODO or Planning
- Board - a specific collection of lists, usually corresponding to a specific project
- Nested task list/subtasks - an ordered list of pieces of text for each card that can each be marked as either completed or not
- Tag - a category that can be added to a card
- Overview (of a board) - the positions and attributes of the cards, lists and tags visible to the user
- Server (App) - the server-side application which contains the information about all boards
- Client (App) - the client application which connects to the server application; this is the app that users run
- Active Board(*) - the board that is currently shown in the Overview and thus the only board which the user can edit right now
- Switch to a board(*) - change the Active Board to another board
- List of Boards/Board List(*) - a list of all of the boards that a user has access to.

(*) - related to the Multi-Board Epic

## Functional Requirements

1. ### Basic Features
    - As a user I want to be able to create cards so that I can keep track of new tasks
        - must have an add card button at the end of each list
        - after pressing the button the user must be asked about the card's title
    - As a user I want to be able to edit cards that have already been created
        - it must be possible to edit the title of the card from the overview
    - As a user, I want to be able to delete cards, if they are no longer needed in any list
        - it must be possible to delete cards from the overview
    - As a user, I want to move cards so that I can organize them in groups (lists) or keep track of their progress and also prioritize them within a list
        - it must be possible to move cards between lists by drag-and-drop
        - it must be possible to move cards within a list by drag-and-drop
    - As a user I want to be able to create lists so that I have separate groups of tasks or so that I can have a different list for different progress levels for a task
        - there must be button through which a new list can be created
        - it must be possible to fill out the list name when creating it
    - As a user I want to be able to edit lists so that I can change the list's name without losing the tasks within that list
        - it must be possible to rename a list from the overview
    - As a user I want to be able to delete lists if it is no longer needed
        - it must be possible to delete a list from the overview
    - As a user I want to be able to move lists so that I can place them where they make the most sense
        - it must be possible to reorder lists by drag-and-drop
    - As a user I want to work on a board with other users so that we can share common tasks or work on a common goal/project
        - it must be possible to work on the same board with other users at the same time
        - the app must not break/crash when two users make a change to the same part of a board at the same time
        - any changes made to a board by any user must be saved to the server so they can be retreived when a user opens that board on their client app again
        - those changes must be persistent, i.e. they must not be lost when the server restarts 
        - the board (and its Overview) must automatically synchronize for all users that have access to it when a change is made
        
2. ### Multi-Board
    - As a user, I want to create more boards, so I can better better organize my tasks and so that not everyone works on the same board
        - creating a board should require the user to enter a name (title) for that board
        - created boards should appear in the user's List of Boards
        - created boards do not appear in any other user's List of Boards, and thus they cannot be edited by any other user, unless explicitly shared
        - creating a board should automatically switch to that board
        - all of the boards should be saved seperately on the server, meaning that boards should not in any way reference any other boards and that the server should be able to store an arbitrary number of boards
        - the boards should be able to be edited independently of one another, meaning that a change to one board has no effect on any other board
        - there could be a "Welcome" page that shows up if a user doesn't have any boards in their Boards List which provides the user with an option to create a new board
    - As a user, I want to have convenient access to all of my boards, so I can efficiently use the app with many boards
        - the List of Boards should be avaliable to be accessed from the Overview, no matter which board is currently active
        - once opened, the List of Boards should provide a way for the user to switch to any of the boards on the List
        - the List of Boards should be stored locally, so that each client app installation, and thus each user, has their own List of Boards
        - that local list of boards should be persistent for a user even if they restart their Client
    - As a user, I want to share my boards with other users, so I can collaborate with them on specific boards
        - for any board on a user's List of Boards there should be a code that they can send to other users, that they can then use to add that board to their List of Boards
    - As a user, I want to join other users' boards, so I can have access to them, and thus be able to edit them
        - when a user enters a code for a board sent to them by another user, the board corresponding to that code should be added to their List of Boards and thus they should have access to it, just like the user who sent them the code
        - the "Welcome" page that shows up if the user's Board List is empty could also offer the user an option to join a board instead of only creating one
        - if an invalid board code is entered, as verified by the server, the user should be notified and nothing should be added to their List of Boards
    - As a user, I want to remove boards from my List of Boards, so it doesn't become cluttered
        - a user should be able to chose to remove any of the boards in their List of Boards
        - removed boards should disappear from the user's List of Boards
        - removed boards should not be deleted on the server and they should not be removed from any other user's List of Boards
    - As a user, I want to delete boards, if they are no longer needed and I don't want anyone else to have access to them
        - a user should be able to chose to delete any board that they can edit
        - deleting a board should delete all information about it from the server
        - deleting a board should remove that board from every user's List of Boards (when they connect to the server)
        - the code of the board should be invalidated thereafter

3. ### Additional Card Details
    - As a user I want to add details to cards so my cards can contain more information and be more useful to myself and others
        - there should be a way to add a text description to each card
    - As a user, I want to have subtasks for each card, so I can subdivide the task that the card represents into smaller and more attainable tasks
        - managing subtasks should be done from the same place that editing the card's description is done
        - there should be a way to add a subtask to a card
        - there should be a way to mark a subtask as completed and to revert that, i.e. to mark is as uncompleted if it has accidentally been marked as completed
        - there should be a way to delete a subtask
        - there could be a way to reorder subtasks within a card
    - As a user I want to edit the cards' details through a single pop-up window, so that I can conviniently edit everything about a card at once
        - through the edit button in the view card window a new pop-up should appear for editing
        - it should be possible to edit the title of the card
        - it should be possible to edit card details - description, nested lists, tags
        - it should be possible to delete cards from the same window
    - As a user I want to be able to view the additional card details without editing them, so I don't accidentally make any changes
        - should have a pop-up that displays information about the card, but does not allow the user to edit the card
        - the information should contain the card title
        - the information should contain the card description
        - the information should contain the nested task list (subtasks)
        
4. ### Tag Support
    - As a user I want to be able to create tags so I can organise and categorise the cards better besides just placing them in different lists
        - it should be possible to create a new tag, which is automatically added to a card 
        - any tags that a card has should appear underneath the card's title in the Overview
    - As a user I want to be able to add and remove tags from cards so I can improve the organisation of the cards in my board
       - it should be possible to add an existing tag to a card 
       - it should be possible to remove a tag from a card, this does not delete the tag from any other card
    - As a user I want to be able to manage tags for the whole board so I can work more efficiently by managing all the tags rather than the singular tags added to cards
       - it could be possible to create a tag for later use, without adding it to a card
       - it could be possible to delete a tag from the whole board, which will remove it from any existing cards and it will no longer show up when the user tries to add a tag to a card

5. ### Customization
    - As a user, I want to be able to customize boards so I can make them more personalized for my needs
        - it should be possible to customize the board's visual appearance (background color, background image) through a board settings page or a pop-up
	    - it could be possible to change the board's name through a board settings page or a pop-up
    - As a user, I want to be able to customize tags, so they can be more useful
        - it could be possible to change the color of a tag when you create it
        - it could be possible to change the name and color of a tag after you create it, which will reflect in every card that tag is used in

6. ### Keyboard shortcuts
    - As a user, I want to be able to manage the board using shortcuts, so that my workflow becomes more efficient, by doing tasks more quickly.
        - it should be possible to add cards and lists using shortcuts
        - it could be possible to add tags using shortcuts
        - it could be possible to move cards using shortcuts
        - it could be possible to move or reorder the lists of the board using shortcuts
        - it could be possible to create and join boards using shortcuts
        - it could be possible to open a board's settings using shortcuts
        - other shortcuts could be included, potentially matching to all functionalities
    - As a user, I want to view all of the shortcuts that I can use at the current moment, so I do not have to remember them
        - there could be a keyboard button (e.g. `Alt`) that you can press which shows appropriate shortcut annotations for every action that you can do using a keyboard shortcut

7. ### Protected Boards
    - As a user, I want to determine what kind of access to give to other users when I share a board with them to ensure the safety of the data in each board and to have granularity in what kind of access I want to give to which users
        - there should be another type of code that users can share that gives "read-only" access to a board, as opposed to "read/write" access with edit permissions like the regular board codes
        - when a user enters that read-only code, the corresponding board should be added to their List of Boards and they should be able to view any information on that board, but they should not be able to edit anything on the board
        - if a user has entered a read-only permissions for a board they should be able to access a read-only code for the board to share it with others, but they should not be able to access the read/write code for that board
        - if a user has read/write permission for a board they should be able to access (and thus be able to share) both the read code and the read/write code for that particular board
    - As a user, I want to upgrade my privileges for a board by entering another code, so that I never have the same board in my List of Boards more than once
        - if a user has "read-only" privileges to a board and they enter the read/write code for that board, they should automatically be "upgraded" to have read/write privileges to that board (with the board showing only once in the board list)

8. ### Manage Server
    - As an administrator, I want to be able to restart the server if I want to alter configurations or if some error happened
        - it should be possible to start the server
        - it should be possible to stop the server
        - it should be possible to restart the server
    - As an admin, I want to be manage everyone's boards, so I can do any changes to them that I deem necessary or remove them if I find them inappropriate
        - it should be possible for the admin to view all of the codes of every board from the server app, so that they can join them, edit them, and even delete them from the client app
        - it could be possible to invalidate certain board codes for a specific board, so that users are locked out of that board or be downgraded to read-only permissions
    - As an administrator, I want to be able to manage the boards on the server remotely, so that I can apply changes requested by normal users
        - there could an admin password that the admin could enter into the Client App to get access to all boards and their codes while they don't have physical access to the server

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
