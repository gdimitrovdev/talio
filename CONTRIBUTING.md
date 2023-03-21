# Contributing
This is a document, meant for internal use by the team that specifies guidelines and instructions for contributing in the project. If you happen to be reading this somewhere other than the internal TUDelft GitLab (perhaps we'll upload the project to GitHub once it's done), keep in mind that this project isn't really open to outside contributions.


## Project structure
Some notable mentions about the files/folders in this project:
- `/server/`, `/client/`, `/commons/` - Java Gradle projects, following the default Gradle directory layout
- `/docs/` - Meeting agendas and minutes, Delivered assignments and reports, and feedback by the TA
- `/gradle/`, `/gradlew`, `/gradlew.bat` - the Gradle wrapper (you don't have to install gradle to build/run this project, in a terminal you can just use `./gradlew` like you would `gradle`)
- `/settings.gradle` - specifies the three Gradle projects, mentioned above
- `/build.gradle` - specifies settings common to all of the three Gradle projects (the projects have additional folders in their own `build.gradle` files)

## Building and Running the project
Make sure that you have the JDK 19 installed and configured. As mentioned before, you don't need to have Gradle installed to work on the project, you can just use the Gradle wrapper. Here are some useful commands:
- `./gradlew build` - compiles all three projects, checks their formatting with checkstyle, and fully tests them. The build will fail if the compilation fails, if there is a checkstyle error or if any test fails. This command should be run every time before pushing of creating an MR, to check if the pipeline will pass. Checkstyle warnings are allowed, but they will be printed on the screen. TODOs will also show up as a warning, so please convert them to issues whenever you can.
- `./gradlew bootRun` - runs the Spring server
- `./gradlew run` - runs the Client application
- `./gradlew checkstyleMain checksyleTest` - runs just the checkstyle

## Using IntelliJ IDEA
If you have already imported the project before and are experiencing issues, a clean import might solve that. Run `git clean -xfd` and open the project in IntelliJ again (perhaps try with `./gradlew build` first).

### Running and building through IntelliJ

#### Server
IntelliJ should automatically have a working Spring config called Main, which should run the server without issues out of the box. If that is not the case, do a clean import, as mentioned above.

However, by default IntelliJ uses a different working directory to gradle, which means that `.gitignore` is not always working quite correctly and leaves some files that it shouldn't and also it means that running the server through IntelliJ and through Gradle will use different database files. To mitigate this, do the following steps:
- **Settings of the server run config > Build and Run > Modify Optoions > Opertaing System > Working Directory (check this)**
- **Settings of the server run config > Build and Run > Working Directory: `$ProjectFileDir$/server`**

#### Client
To run the Client app, create a new run configuration which runs the Client project's Main class. Do the following steps:
- **Settings of client run config > Build and Run > Modify Options > Operating Sustem > Allow Multiple Instances**
- Due to IntelliJ bugs: https://youtrack.jetbrains.com/issue/IDEA-259706 and https://youtrack.jetbrains.com/issue/IDEA-232340/JavaFX-with-11-JDK-IDE-Run-Debug-Configuration-gradle-delegate-doesnt-work-correctly, we need to do the following(in theory it could also be solved by using Java Modules, but this is unforuntaly non-trivial):
  - Download the JavaFX SDK with the same version as the one in the Gradle build configs
  - **Settings of client run config > Build and Run > Modify Options > Java > VM options**
  - **Settings of client run config > Build and Run > VM options: `--module-path="WHEREVER_YOU_DOWNLOADED_JAVAFX_SDK/javafx-sdk-19.0.2.1/lib" --add-modules=javafx.controls,javafx.fxml`**

#### Program arguments
Adding program arguments to the server or the client can be done by:
- **Settings of server/client run config > Build and Run > Modify Options > Java > Program arguments**
- **Settings of server/client run config > Build and Run >  Program arguments:**

### Formatting 
In order to have IntelliJ automatically format the project for you, please do the following steps:
- Install the Checkstyle plugin
- Add a new Checkstyle config from the `/checkstyle.xml` of the project
- **IntelliJ Settings > Tools > Checkstyle > Scan Scope > Only Java sources (including tests)**
- **IntelliJ Settings > Editor > Code Style > Scheme > Project**
- - **IntelliJ Settings > Editor > Code Style > Scheme > Gear Icon > Import Scheme > Checkstyle configuration > find the `/checkstyle.xml` of the project**
- **IntelliJ Settings > Editor > Code Style > General > Line separator > Unix and macOS (\n)**
- **IntelliJ Settings > Editor > Code Style > Java > Method declaration parameters > Align when multiline (uncheck this)**
- **IntelliJ Settings > General > Auto Import > Java > Add unambiguous imports on the fly**
- **IntelliJ Settings > General > Auto Import > Java > Optimize imports on the fly**

## Git Workflow
Make sure to have to following settings set up:
- Username and email corresponding to your name or netid and TUDelft email
- `git config pull.rebase true`
- `git config auto.crlf false`

### Workflow
- Use `git fetch --prune` or `git fetch -p` when fetching. If you see that any branches have been deleted on the remote, delete them locally as well (if you have previously checked them out of couse)
- When working on an issue, don't forget to assign yourself to it and move it into `in-development` on the board

#### Merging into main
- Don't directly push into main, unless it is to for something like uploading a minutes of a meeting
- If there are any open MRs, perhaps review them, before opening yours
- Don't rebase off of or merge with other topic branches, only the main, even if that means that if the topic branches are merged before yours you will have to rebase/merge again
- Checkout your topic branch (after fetching and pulling main) with `git checkout topic` and either merge main into the topic branch `git merge main` or rebase the topic branch on top of main with `git rebase main`. Resolve any conflicts as needed. Run `./gradlew build`. If successful then push your topic branch onto the remote (`git push -u origin topic`) and open the MR for merging the topic into main on GitLab. This should then be a Fast Forward merge
- Move relevant issues to `in-review` on the board
- In the description of the MR use the format `Fixes #5, #6, #14`, so that the issues will be automatically closed once the MR is merged. Alternatively use `Ref #5, #6, #14` in commit messages.
- Assign yourself to the MR and find a reviewer to review your MR (assign them as reviewer on GitLab as well)
- Once the review is done, comment on any threads, do any necessary changes, as requested by the reviewer and as you see fit. Once all of the threads are resolved, the reviewer should approve the MR and the author should merge the MR
- If your MR is an agenda or an assignment or something of that nature, wait until everyone has given an approval at least once to merge
- Once an MR is merged don't forget to close the issues and remove the in-review label
