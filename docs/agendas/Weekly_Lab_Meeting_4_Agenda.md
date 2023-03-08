
# Weekly Lab Meeting 4 - Agenda
*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted in italics, between square brackets [].
The actual minutes don’t have to be in square brackets, but they should keep the italic styling, so that they are easy to distinguish.
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: DW IZ2

Date: Tuesday, 7 March 2023

Time: 14:45
> *The meeting started at: 14:47*

Attendees:
- Team 69: Agnese Ēlerte, Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira, Veselin Mitev
- TA: Alexandra Ioana Neagu

> *Everyone was present at the meeting.*

## Agenda Items
1. Opening by chair
2. Check-in
    1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
    1. Does anyone have any additions/changes to propose for this agenda?
    > *No changes done.*
4. GitLab
    1. Regarding GitLab issues, what is the difference between milestones and epics? Does one milestone have multiple epics, or the other way around?
    > *Issues grouped by issues or milestones(1 is enough,milestone recommended). Milestone group issues supposed to be done in the same sprint.Milestones name: sprint1,sprint 2, etc*
    2. Should an issue on GitLab always correspond to one branch?
    > *In general, yes. In general issues should be done by 1, if multiple people need to work we can split it.*
    3. Why was the number of needed approvals per merge request changed? Can this number be increased or do we have to coordinate this ourselves?
    > *It cannot be modified by us. It will be 1 approval. We can react to a MR instead.*
    4. Can we enable "fast-forward only" merges for the merge requests?
    > *We cannot modify it. We can squash when merging to make the history look cleaner. If everyone works separately there should not be any problems. Push branch, create MR and people look through code by pulling and check it before merging.*
    5. Is it a good practice to use "git rebase" or "git merge" before creating a merge request? If so which one should we use?
    > *General merge main in the feature branch,not sure if rebasing is allowed. if allowed, rebasing is a better practice.*
    6. For the meaningful MR, it two members worked on a branch and merge it, do the two of them pass for this criterion, or only one of them? 
    > *It is ok for 2 to work on a branch this week as long as we have commits. If it is a big part we can just split it in multiple branches. The branches should be merged by those that worked on it. Comments on MR *
    7. Should we set up git pull.rebase?
    > *If we can yes since the history will look cleaner.*
5. Application
    1. Is the way that we split the tasks between the team members good enough?
    > *We should split as 1 person per branch. We should change issues to be more specific. It is good for the beginning it is ok, later the DB will not have much work. We should also change the parts we are working on as everyone must have an idea of what we are doing. A person can work on both things if needed. In the beginning split between client and server, as time goes on we can mix them.*
    2. UI progress
    > *We can use only java code for the UI.*
    3. Database (entities and repositories) progress
    > *Try to reorganize entities as we have a lot of repeated information. We should refactor from the beginning.*
    4. Server progress
    > *Basic methods are implemented.*
    5. How good is our progress on the application currently compared to other teams? Is our pace of work sufficient?
    > *We are quite ahead of other teams. We should only show stuff from the main branch, not the side branches.*
6. Heuristic usability report
   1. Who is going to review our application's usability?
    > *We should find a team to evaluate. Talk to friends and have a mutual review. One team is enough.*
7. Question round
    1. Does anyone have any other questions for the TA before the meeting closes?
    > *We don't have to fully respect the official backlog. If something is extra it will not be graded. If there are no troubles we can follow our own backlog. It is fine to implement features as we see it*
8. Closure
> *The meeting ended at : 15:34*
