# Weekly Lab Meeting 3 - Agenda 
*Notes on formatting:
Proposed places for the minute-taker to record the minutes during the meeting are noted in italics, between square brackets [].
The actual minutes don’t have to be in square brackets, but they should keep the italic styling, so that they are easy to distinguish.
The minutes don’t have to be a word-by-word exact record of what was said, they can be a quick and concise summary of the decisions taken by the team and the answers, feedback and suggestions given by the TA.*

Location: DW PC2 front left

Date: Tuesday, 28 February 2023

Time: 14:45 
> Meeting started at 14:48

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
    > No changes were done to the agenda.
4. Backlog
    1. Review of our backlog and the grading of the backlog. What do the different feedbacks (pass/fail, sufficient, good, excellent, etc.) mean? 
    > Backlog review: having one epic for all the basic requirements and then one epic for each of the advanced requirements is ok, but you should make the epics more clear. You should prioritise epics, even among those in the same category (e.g. should, must), so that you know exactly which ones to do first. Just ordering them is enough, but it needs to be a bit more clear. Within each epic, having "should", etc. for the acceptance criteria is good. For the user stories, see how they are formated in the lecture, for example "I want as <role> to do ..., so that...". Also give a reasoning, which most of them do not have yet. Describe why a user would want to have that. The epics are fine as they are. The prioritisation was understood, but adding numbers could be more clear, instead of simply bulletpoints.
    > Grading: if it is excellent, no improvement is needed. Some parts are pass or fail, e.g. meeting organisation, while most parts are are graded. The grades are: insufficient, sufficient (bare mininum), good (certain degree of understanding), very good (still missing something), and excellent (everything that was expected). These grades do not yet correspond to any numbers, i.e. real grades. 
    2. What other stakeholders should we have? (The lecturer said that the user and admin are acceptable) 
    > In theory, an app has multiple stakeholders besides the user and the admin. Consider what other people have a stake, e.g. people hosting databases, the university, the people who might contribute to the project, and also the developers. These are indirect stakeholders. No user stories are required for them. You should add [around] two more of them but no user stories.
    3. What would be an example for an epic? (The lecturer said that we should have one epic called basic requirements and one epic per advanced requirement) 
    > As noted before, the epics are good as they are now.
    4. How specific should the description of the UI be? 
    > It should not be specific, which is how it seems at the moment. If there is much detail in the backlog it becomes too big. Mention that the app will have an UI, main page, etc., but do not go too in depth, such as button or pop-up. If it is an important feature, you can include it. It is not a problem if you specify. For example, "as I user I want to edit the card in the pop-up so that I can change the task if I mistyped it". 
    5. Should the non-functional requirements be formatted with stories or is the format we use acceptable? 
    > The format is definitely ok and quite in depth. They are ok, because they are not a big part of the backlog. You have a few and they are also categorised.
5. GitLab
    1. Will we get access to the settings of our GitLab repository? (To change the number of approvals needed for a merge request) 
    > Not really. You are just developers, and only TA's, lecturers, etc. have higher authority. Two people is the minimum number of approvers, though some MRs would need one approver. The TA's have decided that they will leave the main branch unprotected, so that we can directly push to them. Two people should do code review to approve the MR. The code review should be visible there, along with a little discussion and comments before the approval, e.g. check this or that. Give meaningful feedback. There is no way to block a MR, but it is possible not to approve it. If only two people approved it, you should discuss that and say why it should (not) be approved. Reverting back is always possible. Moreover, you should not be the ones approving the MR if you worked on that branch. For instance, the other four members should approve it if two members worked on a feature together. For documents (e.g. CoC, Backlog), that is not necessary, but for code going forward, do not approve your own. You cannot modify the number of approvers on a case by case, since GitLab does not distinguish that. I will ask if it is possible and will let you know.
    2. Should GitLab be used as much as possible for communication? (using issues, merge requests, and comments on those) 
    > Yes, use it as much as possible. Make good use of MRs, issues, milestones, tags, etc. Commented issues are helpful for the TA to see everything.
    3. Should MRs be used for everything? (uploading the agenda and minutes, etc.) 
    > No, the documents can be pushed directly on the main branch.
    4. Is doing MRs only when merging into the main branch a good idea? 
    > Not asked during meeting. 
    5. Can we add a CONTRIBUTING.md with instructions, code and configuration standards and other technical aspects of the project? 
    > Yes, with technical information. You can add that. At the end of the quarter you will put instructions on how to run the server in the README.md file. But if you already start it now, it is good. You can add the file to the root directory.
    6. Should we convert the CoC and the agenda of Week 2 to Markdown and upload them to GitLab? 
    > All the documents should be pushed to GitLab. The format does not matter that much. File formats .pdf and .docx are also accepted, but I prefer not using .docx, since with .pdf and .md I can directly see them, and do not need to download. However there is no need to conver those documents already uploaded.
    7. Should GitLab issues be created for tasks far in advance (such as agendas)? 
    > Yes, issues for everything. You can have one recurrent issue every week for agendas. You do not need to create eight, but can just have one about agenda and notes, which you keep open as a reminder. You also need issues for organisation. If you want, you can create one every single week.
6. Questions about the app
    1. Should the (client) app be resizable and responsive? 
    > No, they do not care about that. Does not need to be resizable; a fixed size is ok.
    2. Should it be possible to drag & drop list to reorder them? If not, how should they be ordered? 
    > I cannot answer about requirements. TA's cannot help with requirements. These features which are unclear are completely up to you how to implement them.
    3. Admin questions
        1. Does the admin have a GUI? 
        > Answer to that: we do not know. Since the client did not specify, it is up to you to do it as you see fit. Would this be specified in the Backlog? Definitely.But the example Backlog would be probably worse than yours. It is not very extensive, but rather an example to take inspiration. There are lots of students asking about the admin on Mattermost. For the spring app, a CLI with access to all boards would be fine.
        2. Does the admin use the client app or the server app? 
        > You could make an UI on the client side with a password that gives authorisation or have something on the server side with all priviledges. 
        3. Should anyone be able to be an admin or only if they have access to the server app/are on the computer running the server app? 
        > Up to you.
7. Code of Conduct (Low Priority)
    1. What is the issue with the point “Decision Making”? Is a majority vote not a good way to to solve the problem of one person disagreeing? 
    > I want more detail on it. It is a bit vague: "If the process does not work, get the TA's opinion. What if someone disagrees? What does it mean to ask the TA's opinion first? Formulate more clearly: her opinion can change the person's opinion. Moreover, at your last sentence you say consensus would work in majority of cases. But you do not say that in the CoC, but put rules for when it does not work out.
    2. Should we try to improve the CoC or is it acceptable the way it is right now? 
    > Overall very good. A few sections without excellent. Some changes.
8. Question round
    1. Does anyone have any other questions for the TA before the meeting closes? 
    > How to upload the Backlog? Keep the draft, and write "draft" in the title. Leave the draft that was there, and add another file which is the final version. You can rename the draft in the main branch, merge the other branch. Feedback in the agenda? You can move it if you want. They are just formative feedback. The feedback this week will include a potentially shippable product, which is the state of the app that you have on the main branch. This rubric was added this week, and will be insufficient for all, since it is non-applicatble.
    2. TA things to us
    > Deadlines: final Backlog and Git Assignment due on Friday. The git instructions were released later, thus the deadline for Friday is not a hard one. You can have an extension until Monday if you text me first on mattermost.
    > The TA's will look at the main branch only, so always merge into main. Features on other branches are not graded. Keep the final version always on main branch. 
    > Last thing: start coding once done with Git assignment. The setup is hard at the beginning; do not get discourage with Spring: I can recommend some tutorials. 
1. Closure 
> Meeting ended at 15:29
