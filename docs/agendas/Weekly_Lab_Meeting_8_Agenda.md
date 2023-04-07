# Weekly Lab Meeting 8 - Agenda

*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted as a quote ("> Response").
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: *DW IZ2*

Date: *Tuesday, 4th of April 2023*

Time: *14:15*
> Meeting started at 14:16

Attendees:
- Team 69: Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira, Veselin Mitev
  - Chair: Pedro Gomes Moreira
  - Minutetaker: Veselin Mitev
- TA: Alexandra Ioana Neagu
> Everyone was present

## Agenda Items
1. Opening by chair
2. Check-in
    1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
    1. Does anyone have any additions/changes to propose for this agenda?
    > Make any necessary changes to the agenda and upload them so that everyone can see them
4. Admin functionality
    1. For the admin functionality, we created another controller that provides endpoints, which are admin/boards, admin/fill, admin/clear, admin/refill, etc., which can be accessed through a web browser. Does this satisfy the requirement of having a server password? Our idea is that these endpoints are unknown to the user and cannot be accessed through the Talio application.
    > Sebastian was not sure. We should ask in the Town Square. He was inclined to say that it meets the requirements, but we shoul make sure.
    2. For the requirement of deleting boards from the overview as an admin: if the users can delete boards, admins can also do that. Does this satisfy the requirement?
    > It satisfies the requirement.
5. Testing
    1. What percentage of test coverage should we aim for? We currently have almost 100% for commons, 50% for server, and 0% for client.
    > We should aim for around 80% coverage. We should focus on the client, because we need to have testing on all parts of the program. We only need to test the ServerUtils class in the Client.
    2. Are the server tests sufficient if they are simple? Should we aim to write more extensive tests?
    > We should have more complex tests. The simple tests meet the coverage requirement. More complex tests involve testing for edge cases.
    3. How should we test the UI since Sebastian said we should extract the logic from controllers to services, and our logic is manipulating UI and not data?
    > The ServerUtils should be tested. We should hear back about whether we should test the JavaFX controllers.
6. Product Showcase
    1. Vesko - Did the server-client connection, but some errors happened. Fixed the list and card overview components (not merged yet).
    2. Pedro - Modified common classes, finished the ShareBoard, created BoardSettings, did tests for the server controllers, fixed tags in card popup. Next task is adding tag functionality to BoardSettings
    3. Dimana - Did tests for server services. Currently working on BoardSettings: she will finish it soon. 
    4. George - Fixed issues for the client-server connection. After that, the application was fixed in many aspects and the basic requirements were done. Worked on saving recent boards locally. Currently working on reordering subtasks.  
    5. Agnese - researched about client side testing and tried her best to do it but could not. Implemented most of the keyboard shortcuts, except for C and T, currently working on these.
    6. Matei - working on UI Fixes, which are changes to the UI to improve coherency and usability. Currently working on making the connection smoother.
    > We should try not to introduce bugs on main when doing an MR. A good improvement from the version a few days ago. Matei is a bit behind in the code contributions, but he has time to catch up and he was not flagged by the automatic system.
7. Task distribution 
    1. What should we focus more on: doing the remaining epics, testing, refactoring code, making the UI nicer?
8. Additional Questions
    1. Does anyone have any other questions for the TA before the meeting closes?
    > Dimana asked a question about the Teamwork Self Reflection and the TA said that she doesn't need to do anything more.
9. Closure
    > 14:41
