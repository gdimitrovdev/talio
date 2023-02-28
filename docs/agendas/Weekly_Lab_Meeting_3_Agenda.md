# Weekly Lab Meeting 3 - Agenda 
*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted in italics, between square brackets [].
The actual minutes don’t have to be in square brackets, but they should keep the italic styling, so that they are easy to distinguish.
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: DW PC2 front left

Date: Tuesday, 28 February 2023

Time: 14:45 
> [14:48]

Attendees:
- Team 69: Agnese Ēlerte, Georgi Dimitrov, Dimana Stoyanova, Matei Bordea, Pedro Gomes Moreira, Veselin Mitev
- TA: Alexandra Ioana Neagu

> [Everyone attended the meeting] 

## Agenda Items
1. Opening by chair
2. Check-in
    1. Make sure everyone has a copy of the agenda open
3. Approval of the agenda
    1. Does anyone have any additions/changes to propose for this agenda? 
    > [Do any necessary changes to the agenda if needed] 
4. Backlog
    1. Review of our backlog and the grading of the backlog. What do the different feedbacks (pass/fail, sufficient, good, excellent, etc.) mean? 
    > [One epic for basic requirements, other for each of the advanced: ok, but we can make it more clear which apics. Prioritise epics, even between those which are should, must; which ones should be done first? Just ordering them is enough, but it needs to be a bit more clear. Within each epic, should must is good. For the user stories, see themf rormated the way there are in the lecture, for example I want as (my role) to do this so that something. Also give a reasoning. Most of them don't have a reason. Why a user would want to have that. The correct model. The epics are fine. The prioritisation was understood, but adding numbers could be more clear.] 
    2. What other stakeholders should we have? (The lecturer said that the user and admin are acceptable) 
    > [In theory, an app has multiple stakeholders besides the user and the admin. What other people have a stake. People hosting databases, the university, the people who might contribute, also the developers. More indirect stakeholders. No user stories for indirect stakeholders. Two more of them but no user stories.] 
    3. What would be an example for an epic? (The lecturer said that we should have one epic called basic requirements and one epic per advanced requirement) 
    > [Record response from TA] 
    4. How specific should the description of the UI be? 
    > [No, too specific. Too much detail for backlog, otherwise becomes too big. The app will have an UI, main page, but don't goo to in depth: button, pop-up. If it is an important feature, can include it. No problem if specify. As I user I wanto to edit the card in the popup so that I can change the task if I mistype] 
    5. Should the non-functional requirements be formatted with stories or is the format we use acceptable? 
    > [Defininetely ok, quite in depth. They are ok, because they are not a big part of the backlog. A few and also categorised] 
    6. Grading
    > [If it is excellent, no improvement. Some parts are pass or fail, others are graded. Meeting organisation is pass/fail, most of the others are graded. Insufficien, suff (bare min), good (degree of understaiding), very good (still missing something), excellent (everything that was expected). Don't correspond to any numbers yet. ]
5. GitLab
    1. Will we get access to the settings of our GitLab repository? (To change the number of approvals needed for a merge request) 
    > [Not really. We are just developers, TAs etc have higher priority. 2 is the minimum number. Some would need one. They will leave main branch unprotected, so that directly push to them. Two people should do code review, to approve the MR. See code review there, little discussion, commenting before approving, check something. Give meaningful feedback. Don't approve it to block. Only 2 people approved, why? Discussions say it should not be approved. Revert back always possible. You should not be the ones approving the MR if you worked on that branch. Other 4 people if 2 people worked together. For documents, no. Backlog, coc is ok, but for code going forward, don't approve your own. They are not too harsh. Increase number of approvers, TA will look into that. You cannot modify on a case by case, GitLab does not distinguish that. She will ask and let us know.]
    2. Should GitLab be used as much as possible for communication? (using issues, merge requests, and comments on those) 
    > [Yes, use it as much as possible. Outside of MRs, issues, milestones, tags. Commented issues are helpful for her to see everything.]
    3. Should MRs be used for everything? (uploading the agenda and minutes, etc.) 
    > [No, the documents can be pushed directly]
    4. Is doing MRs only when merging into the main branch a good idea? 
    > [Record response from TA]
    5. Can we add a CONTRIBUTING.md with instructions, code and configuration standards and other technical aspects of the project? 
    > [Yes, more technical. We can add that. In the README.md, at the end of the qruater we will put instructions on how to run the serve.r I few already start, it is good. Add it to the root directory.]
    6. Should we convert the CoC and the agenda of Week 2 to Markdown and upload them to GitLab? 
    > [All the documents should be pushed to GitLab too. The format does not matter that much. Pdf and docx also, but she prefers not using docx; while pdf and md she can directly see them. Those already uploaded, no need to convert.]
    7. Should GitLab issues be created for tasks far in advance (such as agendas)? 
    > [Yes, everything. One issue recurrent every week for agendas. Don't create 8, having one about agenda and notes. Keep open as a reminder. Also need issues for organisation. If we want, we can create one ervery single week]
6. Questions about the app
    1. Should the (client) app be resizable and responsive? 
    > [No, they don't care about that. Doesn't need to be resizable, fixed size is ok]
    2. Should it be possible to drag & drop list to reorder them? If not, how should they be ordered? 
    > [she can't answer; requirements TAs can't ask. She cannot help, these features which are unclear is completely up to us how to implement them. ]
    3. Admin questions
        1. Does the admin have a GUI? 
        > [Answer to that: we don't know. Since client did not specify, it is up to us to do it as we see fit. Would this be specified in the Backlog? Definitely. Example would be worse than ours. It is not very extensive, more like and example to take inspiritaion. Lots of students asking this on Mattermost. We could make an UI on the client side with a password that gives authorisation OR on the server size with all priviledges. For the spring app, CLI with access to all boards? Would be fine]
        2. Does the admin use the client app or the server app? 
        > [Any of them] 
        3. Should anyone be able to be an admin or only if they have access to the server app/are on the computer running the server app? 
        > [Record response from TA] 
7. Code of Conduct (Low Priority)
    1. What is the issue with the point “Decision Making”? Is a majority vote not a good way to to solve the problem of one person disagreeing? 
    > [Ok, but she wants more detail on it. If it doesn't work, get the TA's opinion. A bit vague. What if someone disagree? What does it mean the TA's opinion first? Her opinion can change the person's opinion. Formulate more clearly. Last sentence, consensus would work in majority of cases, but we don't say that in the CoC, but put rules for when it doesn't work out. ] 
    2. Should we try to improve the CoC or is it acceptable the way it is right now? 
    > [Overall very good. Few sections without excellent. We will change. ] 
8. Question round
    1. Does anyone have any other questions for the TA before the meeting closes? 
    > [Upload in your file. Keep the draft ,put it in the title. Leave the draft that was there, add another one which is the final version. Rename draft in the main branch, merge. Feedback in the agenda? Yes wil be there. We can move it if we want. Just formative feedback. Potentially shippable product: state of the app, what we have on main branch. Rubric added this week, which N/A (insufficient for all). ]
    2. TA things to us
    > [Deadlines: final Backlog and Git assignment due on Friday. Git later release, deadline for Friday is not a hard one. Extension until Monday if we text her first. Mattermost. TA's will look at main branch, so always merge into main. Features on other branches are not graded. Final version always on main branch. Last thing: start coding once done with Git assignment. Setup is hard at the beginning. Spring: don't get discouraged; she can recommend some tutorials]
1. Closure 
> [15:29]
