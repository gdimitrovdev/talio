Weekly Lab Meeting 6 - Agenda
*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted in italics, between square brackets [].
The actual minutes don’t have to be in square brackets, but they should keep the italic styling, so that they are easy to distinguish.
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: DW IZ2

Date: Tuesday, 21 March 2023

Time: 14:45
> The meeting started at: 14:50

Attendees:
- Team 69: Agnese Ēlerte, Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira, Veselin Mitev
- TA: Alexandra Ioana Neagu

> Everyone attended the meeting

## Agenda Items
1. Opening by chair
2. Check-in
    1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
    1. Does anyone have any additions/changes to propose for this agenda?
    > No additions/changes
4. Questions regarding the update to the state of a board
   1. Choosing between full and partial refresh (refreshing the whole UI every time there is an update to the 
   board that is opened or partial refresh where only the relevant elements of the UI are refreshed
   > Partial refresh is recommended. Any changes that take place should be rendered within 1 second, and in order to do that we will probably need to implement partial refresh instead of a full one. In order to do changes that affect multiple elements (for example the card drag-and-drop), we can just refresh multiple lists, so we can send update requests to both of them as long as this takes less than 1 second. The best practice is to have endpoints for all objects, so we need to have CRUD endpoints for all our entities (as we currently do), but also have the needed websocket endpoints for every entity. For most of our refresh-related issues, it is recommended to choose the simplest solution that doesn't impact the performance too much.
   2. Sending the board of the stage between the server and the client
      (when there is an update, should we send the state of the whole board or send only the changed parts of the state)
   > This could work, however we need to be aware of the 1 second rule. Probably this method will not fit in the 1 second criteria, so it will be best to use rest api calls for the client -> server connection, and websockets for the server -> client connection. We don't need to handle 2 people editing the same component at the same time, however we need to handle different updates at the same time (which is already handled by the auto-synchronization).
5. Heuristic usability report
   1. What is the difference between the draft and the final product?
   > The draft does not  count for the grade. Similar to the backlog, we should make it as good as possible, in order to receive as much feedback as possible so that our end product will be the best version.
   2. When should we let the team that we are working with do an evaluation for us, since our UI is not functional, can we wait for example a week,   before giving our project over to them?
   > We can use UI schemas that are as accurate to the final product as possible, and then we can review those schemas. We can give the other team instructions on how to evaluate our product (every member of the other team should evaluate our product separately). After we receive feedback, we can look at the frequency and severity of problems in each individual review. The more UI that we provide in the mock-ups, the better.
   3. How do we distribute our application to them? Do we work with them in person?
   > We don't have to work with them in person, we just give them the mock-ups. We should tell the other team exactly how to evaluate our product.
6. Application - show what functionalities we have merged into main 
   > We are currently on track. We should focus on handling multiple open clients and on establishing the connection between the server and the client. We should use weights/time for issues, so that our progress on the application can be tracked better.
7. Work distribution
   1. Is the way that we have distributed the work from the last meeting to now okay?
      (Dimana - refresh method + fixing a bug, Matei - work on the list UI element , Veselin - sync the server and client side, 
       Pedro - refactoring the Board and List UI elements, Agnese -, George - web sockets)
   > The work distribution is good, everyone has worked both on the server and the client side.
   2. Does the TA have any suggestions about the work distribution for next week?
   > Usually most teams have 4 people on the server side and 2 on the client side, however the reverse is fine for this week as we are behind on the client side. We should change the people that work on the frontend/backend frequently (at least once every 1-2 weeks).
8. Question round
    1. Does anyone have any other questions for the TA before the meeting closes?
    > No questions.
9. Closure
    > The meeting ended at: 15:30
