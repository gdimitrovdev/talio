# Weekly Lab Meeting 7 - Agenda

*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted as a quote ("> Response").
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: *DW IZ2*

Date: *Tuesday, 27 March 2023*

Time: *14:45*
> Record start time of meeting

Attendees:
- Team 69: Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira
  - Chair: Veselin Mitev
  - Minutetaker: Agnese Ēlerte
- TA: Alexandra Ioana Neagu
> Record who attended the meeting

## Agenda Items
1. Opening by chair
2. Check-in
   1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
   1. Does anyone have any additions/changes to propose for this agenda?
   > Make any necessary changes to the agenda and upload them so that everyone can see them
4. Process Questions
   1. Should we do time tracking for the code reviews of MRs? Is it fine if some members do that and others don't
   > Record response by the TA
5. Heuristic Usability Evaluation
   1. What should the Abstract section be? Is that a summary of the whole report? Do we need it?
   > Record response by the TA
   2. What should the Introduction section be? Is that just background information about the report, like why we are doing it and why such a report is useful?
   > Record response by the TA
   3. Since we have not received the feedback yet, can we get some general feedback about the HUE report? Is the structure good? Are there any major mistakes?
   > Record response by the TA
6. Teamwork Self-Reflection
   1. Any suggestions/ideas for the 3 situations that we have to talk about in the Teamwork Self-Reflection?
   > Record response by the TA
7. Product Pitch
   1. Slides or an app demo?
   > Record response by the TA
   2. Do we need cameras or anything like that?
   > Record response by the TA
   3. Do you have any suggestions on how to do the Pitch?
   > Record response by the TA
   4. Do we need to do a video for the Draft, or can we submit a script and/or slides?
   > Record response by the TA
8. Product Questions
   1. Admin funcionality? Does getting codes to all boards furfill the admin parts of the backlog?
   > Record response by the TA
   2. Do we need to worry about security at all?
   > Record response by the TA
   3. Is extensibility important
   > Record response by the TA
   4. We already mentioned this to you, but for password protection we plan to do two invite codes (one gives you only read permissions for the board), instead of an invite code and a password. This, we think, has a number of advantages:
      1.  Easier for the user (it's easier to share a single code instead of a code and a password),
      2.  Better extensibility (the system can be extended to add more types of codes - time-sensitive codes, codes with custom restrictions),
      3.  Easier to implement (since password protection is the lowest priority, we plan to do that last, and adding another invite code is easier than adding a password, both serverside and clientside).
   
      Given all of this, would doing the two codes system be a complete implementation of the Password Protection Epic in the Backlog? I have already asked Sebastian privately and he was more than fine with it, but we just wanted to make sure, since his answer was before the Backlog.
   > Record response by the TA
   5. Do we need a server connection indicator in the UI? It came up in the HUEs, but we thought that if there was a connection issue we would just bring the user back to the connection screen and tell them that there was an error.
   > Record response by the TA
   6. In the Connection Screen, should we have seperate fields for the IP and Port, or just a URL? Personally, I don't see any use cases which warrant having them seperately, but there was an inconclusive discussion about that between the team and it came up in the HUEs. 
   > Record response by the TA
9. Product Showcase
   1. Vesko - Did the server-client connection (hopefully :D), but it has not been integrated into the rest of the app.
   2. Pedro - Did JoinBoard, CreateBoard and ServerConnection (merged,working); currently working on modifying common classes
   3. Dimana - created Home board UI scene (merged, working), added the actual functionality to the UI scenes created by Pedro and me by calling   methods from ServerUtils that I implemented.
   4. George - drag and drop betweeen lists and drag and drop within a list (merged, working)
   5. Agnese - implemented and connected the card pop-up (merged, working)
   6. Matei - made the board into a scene and finish its implementation (merged, working)
   > Record feedback by the TA
10. Additional Questions
   1. Does anyone have any other questions for the TA before the meeting closes?
   > Record any additional questions and responses here, even if they were asked earlier
11. Closure
   > Record end time of meeting
