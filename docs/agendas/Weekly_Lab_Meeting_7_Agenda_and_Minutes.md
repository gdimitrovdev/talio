# Weekly Lab Meeting 7 - Agenda

*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted as a quote ("> Response").
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: *DW IZ2*

Date: *Tuesday, 27 March 2023*

Time: *14:45*
> 14:45

Attendees:
- Team 69: Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira
    - Chair: Veselin Mitev
    - Minutetaker: Agnese Ēlerte
- TA: Alexandra Ioana Neagu
> All team members attended the meeting.

## Agenda Items
1. Opening by chair
2. Check-in
    1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
    1. Does anyone have any additions/changes to propose for this agenda?
   > No changes or additions were made.
4. Process Questions
    1. Should we do time tracking for the code reviews of MRs? Is it fine if some members do that and others don't
   > It would be useful to do time tracking for code reviews of MRs.
5. Heuristic Usability Evaluation
    1. What should the Abstract section be? Is that a summary of the whole report? Do we need it?
   > The abstract section is not mandatory.
    2. What should the Introduction section be? Is that just background information about the report, like why we are doing it and why such a report is useful?
   > The introduction section should have quick introduction about what HUE is, what it is based on and why it is useful. It should not be too long because the report should not exceed word limit. Pictures (screenshots) of the app should be a part of the methology section (rather than in the introduction), there should be a prototype subsection in the methodology section with the pictures.
    3. Since we have not received the feedback yet, can we get some general feedback about the HUE report? Is the structure good? Are there any major mistakes?
   > Prioritization was not described in our draft. We should mention frequency and severity for each problem. It could be a bulletpoint list of problems with severity and frequency. We should describe how we chose the severity/frequency of the problem. We should follow the way they did in the lectures (last year it was a scale 1-5, we can make a different one). Frequency scale is a combination between how many of the evaluators reported the issue and how often users would encounter it in our opinion. Problems should be described, but not in great detail (3-4 lines for just the problem description). Mention the heuristic, enough to mention only 1, but multiple can be mentioned. The report can have a subsection for each heuristic, mentioning the problems connected with the heuristic and their prioritization. The heuristic subsections we have right now are fine. Severity and frequency of a problem should be described before prioritization.
6. Teamwork Self-Reflection
    1. Any suggestions/ideas for the 3 situations that we have to talk about in the Teamwork Self-Reflection?
   > It should be situations when you were working, had a struggle and then improved. Mention the things we learned during the process. This assignment is pass/fail.
7. Product Pitch
    1. Do you have any suggestions on how to do the Pitch?
   > Video should be like a demo with an app showcase. Presentation (slides) is not important. Each of us should be showing the features of the app. Inner workings of the app is not that important to show, but it should be mentioned (code should not be shown). For example, show a functionality, and mention how it was implemented (escpecially if it is very well implemented). You should be talking while you're showcasing and film yourself. Software suggestion - OBS studio, using zoom/discord, but there are no requirements for the video software
    3. Do we need to do a video for the Draft, or can we submit a script and/or slides?
   > For the draft, there is no mandatory format. It is ok to record some parts, some can be just a script. It is ok to say that there are functionalities that will have not yet been implemented and will be implemented after. A suggestion - video must look pleasant to look at, it is nice to introduce the next presenter. Keep in mind, it takes a while to record 2 minutes of video.
8. Product Questions
    1. We already mentioned this to you, but for password protection we plan to do two invite codes (one gives you only read permissions for the board), instead of an invite code and a password. This, we think, has a number of advantages:
        1.  Easier for the user (it's easier to share a single code instead of a code and a password),
        2.  Better extensibility (the system can be extended to add more types of codes - time-sensitive codes, codes with custom restrictions),
        3.  Easier to implement (since password protection is the lowest priority, we plan to do that last, and adding another invite code is easier than adding a password, both serverside and clientside).

       Given all of this, would doing the two codes system be a complete implementation of the Password Protection Epic in the Backlog? I have already asked Sebastian privately and he was more than fine with it, but we just wanted to make sure, since his answer was before the Backlog.
   > If Sebastian confirmed that it is ok, then yes. We should ask on mattermost (town square), so there's proof that we were allowed to implement the feature this way.
    2. Do we need a server connection indicator in the UI? It came up in the HUEs, but we thought that if there was a connection issue we would just bring the user back to the connection screen and tell them that there was an error.
   > If it was mentioned in HUE, it is better to add it. We could showcase little pop-up in the sever connect scene with a message that informs the user about the disconnect.
    3. In the Connection Screen, should we have seperate fields for the IP and Port, or just a URL? Personally, I don't see any use cases which warrant having them seperately, but there was an inconclusive discussion about that between the team and it came up in the HUEs.
   > Separate IP and Port are not needed, just the URL.
9. Product Showcase
    1. Vesko - Did the server-client connection (hopefully :D), but it has not been integrated into the rest of the app.
    2. Pedro - Did JoinBoard, CreateBoard and ServerConnection (merged,working); currently working on modifying common classes
    3. Dimana - created Home board UI scene (merged, working), added the actual functionality to the UI scenes created by Pedro and me by calling   methods from ServerUtils that I implemented.
    4. George - drag and drop betweeen lists and drag and drop within a list (merged, working)
    5. Agnese - implemented and connected the card pop-up (merged, working)
    6. Matei - made the board into a scene and finish its implementation (merged, working)
   > Task creation thing looks good. UI looks clean, usability is good.
10. Proposed Work Distribution
1. Vesko - ...
2.
> Nothing was discussed
11. Additional Questions
1. Does anyone have any other questions for the TA before the meeting closes?
> Additional comments from the TA: there are only 10 days left so think carefully what features to implement. Don't push big features last minute, gitlab will be down in the last minute. Testing rubric should be done until thursday. 25 minute TA meetings in the future, timeslot might change.
> Additional questions that were asked during the meeting: admin fuctionality - admin can see all the codes for joining the boards, and then the admin can do whatever the user can. This would fullfill the backlog requirements for admin. Security for the app is not a big concern. Users have no malicious intent, no hacking. Users can be stupid, for example, trying to add a card with an empty title. Extensibility of the app and it's features is important to an extent, the features need to be somehow well made so that they can be extended, but this is not a rubric for extensibility, but quality of the features rubrik might consider how well implemented the features are. The extensibility can also be mentioned in the pitch.
12. Closure
> 15:28

