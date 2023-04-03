# Weekly Lab Meeting 8 - Agenda

*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted as a quote ("> Response").
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: *DW IZ2*

Date: *Tuesday, 4th of April 2023*

Time: *14:15*
> Record start time of meeting

Attendees:
- Team 69: Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira, Veselin Mitev
  - Chair: Pedro Gomes Moreira
  - Minutetaker: Veselin Mitev
- TA: Alexandra Ioana Neagu
> Record who attended the meeting

## Agenda Items
1. Opening by chair
2. Check-in
    1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
    1. Does anyone have any additions/changes to propose for this agenda?
    > Make any necessary changes to the agenda and upload them so that everyone can see them
4. Admin functionality
    1. For the admin functionality, we created another controller that provides endpoints, which are admin/boards, admin/fill, admin/clear, admin/refill, etc., which can be accessed through a web browser. Does this satisfy the requirement of having a server password? Our idea is that these endpoints are unknown to the user and cannot be accessed through the Talio application.
    > Record answer from the TA
    2. For the requirement of deleting boards from the overview as an admin: if the users can delete boards, admins can also do that. Does this satisfy the requirement?
    > Record answer from the TA
5. Basic requirements
    1. For the requirement "To open task details through double-click, so I can add further details": can we also do this with a single click?
    > Record answer from the TA
6. Testing
    1. What percentage of test coverage should we aim for? We currently have almost 100% for commons, 50% for server, and 0% for client.
    > Record answer from the TA
    2. Are the server tests sufficient if they are simple? Should we aim to write more extensive tests?
    > Record answer from the TA
7. Product Showcase
    1. Vesko - Did the server-client connection, but some errors happened
    2. Pedro - Modified common classes, finished the ShareBoard, created BoardSettings, did tests for the server controllers, fixed tags in card popup. Next task is adding tag functionality to BoardSettings
    3. Dimana - Did tests for server services. Currently working on BoardSettings. 
    4. George - Fixed issues for the client-server connection. After that, the application was fixed in many aspects and the basic requirements were done. Currently working on saving recent boards locally
    5. Agnese - researched about client side testing and tried her best to do it but could not. Currently working on keyboard shortcuts.
    6. Matei - currently working on UI Fixes, which are changes to the UI to improve coherency and usability
    > Record feedback by the TA
8. Task distribution 
    1. What should we focus more on: doing the remaining epics, testing, refactoring code, making the UI nicer?
10. Additional Questions
    1. Does anyone have any other questions for the TA before the meeting closes?
    > Record any additional questions and responses here, even if they were asked earlier
11. Closure
    > Record end time of meeting
