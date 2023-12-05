# TheAppengers

Our Project: Collaborative Whiteboard 

Team Members: Rahul Bangre, Harsh Dave, Reyansh Patange, Armeen Talwandi

[Project Description](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Project-Description)

[Meeting Minutes](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Meeting-Minutes)

[Requirements](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Requirements)

[Design](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Design)

[Discussion](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Discussion)

## Installation Guide
For your convenience, we have provided 3 methods of installing and running the various.  
There are two ways of running the application locally, the server can be run in a docker container or through the terminal.  

[Latest Release - 2.0.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/2.0.0)

[Release 1.2.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/1.2.0)

[Release 1.1.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/1.1.0)

[Release 1.0.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/1.0.0)

### Installing the Cloud Deployed version of the Appenger's Whiteboard (Recommended)

The first and simplest option to use the Appenger's Whiteboard is by simply navigating to
`./releases/final/remote/`. Here you will find `AppengersWhiteboard_REMOTE.dmg` installer for `macOS`. This version of the client has
been configured to connect to our Google Cloud Platform instance of our server and our database hosted on Google Cloud SQL. 
Once you install this, you are good to go to use the cloud deployed version of the Appenger's Whiteboard.

##### Notes: 
- Avoid drawing too many strokes on whiteboard too quickly. Due to limitations put on our server by GCP, if too many requests are sent at once from the client, the connection might be refused by the server causing the app to crash. If this happens, please restart the app and continue using as normal.
- Performance might be a little slow or laggy while using the remote deployed version of the app due to the fact that it is deployed on the cloud on a budget-friendly instance.

### Installing a local version of the Appenger's Whiteboard (Backup)

Running the local version of the client has much better performance due to the fact that the server, database, and client are running locally. So if you have time, please do experiment with this version of our app.
You can find local versions of the local client installer at `./releases/final/local/` or `./releases/final/docker-local/`. It is called `AppengersWhiteboard_LOCAL.dmg`. This same client can be used for both methods of running the server (below).

#### Running Server through Docker
This is incredibly easy to do. Ensure you have Docker installed on your computer. **Simply navigate to the root of this repository** and execute:
```bash
./run-server-docker.sh
```
The server is now running, and you can use the local version of the Appenger's Whiteboard.

#### Running the server locally with JAR file
This is also very easy to do, ensure you have Java installed on your computer. Then **from the root of the repository** execute the following command in the terminal.
```bash
java -jar ./releases/final/local/server-all.jar
```
The server should now be running on your computer, and the local version of the Appenger's Whiteboard can be run.

##### Notes:
- Both local versions of the server use a local sqlite database. The docker method will create the database in the container. The JAR method will create/use a database.sqlite file in the root directory.

The behaviour of the client is identical in all forms of the installation. If the server is running locally, then data is persisted locally.  

If the remote client is used, then the data is persisted on the cloud and users running the remote version of the app can access the same data across devices thus making the application collaborative.  

If the local client is used, then the data is persisted on the local database and multiple instances of the local version on the same device will be synchronized.

[//]: # ()
[//]: # ()
[//]: # (## Getting started)

[//]: # ()
[//]: # (To make it easy for you to get started with GitLab, here's a list of recommended next steps.)

[//]: # ()
[//]: # (Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom]&#40;#editing-this-readme&#41;!)

[//]: # ()
[//]: # (## Add your files)

[//]: # ()
[//]: # (- [ ] [Create]&#40;https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file&#41; or [upload]&#40;https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file&#41; files)

[//]: # (- [ ] [Add files using the command line]&#40;https://docs.gitlab.com/ee/gitlab-basics/add-file.html#add-a-file-using-the-command-line&#41; or push an existing Git repository with the following command:)

[//]: # ()
[//]: # (```)

[//]: # (cd existing_repo)

[//]: # (git remote add origin https://git.uwaterloo.ca/atalwand/theappengers.git)

[//]: # (git branch -M main)

[//]: # (git push -uf origin main)

[//]: # (```)

[//]: # ()
[//]: # (## Integrate with your tools)

[//]: # ()
[//]: # (- [ ] [Set up project integrations]&#40;https://git.uwaterloo.ca/atalwand/theappengers/-/settings/integrations&#41;)

[//]: # ()
[//]: # (## Collaborate with your team)

[//]: # ()
[//]: # (- [ ] [Invite team members and collaborators]&#40;https://docs.gitlab.com/ee/user/project/members/&#41;)

[//]: # (- [ ] [Create a new merge request]&#40;https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html&#41;)

[//]: # (- [ ] [Automatically close issues from merge requests]&#40;https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically&#41;)

[//]: # (- [ ] [Enable merge request approvals]&#40;https://docs.gitlab.com/ee/user/project/merge_requests/approvals/&#41;)

[//]: # (- [ ] [Set auto-merge]&#40;https://docs.gitlab.com/ee/user/project/merge_requests/merge_when_pipeline_succeeds.html&#41;)

[//]: # ()
[//]: # (## Test and Deploy)

[//]: # ()
[//]: # (Use the built-in continuous integration in GitLab.)

[//]: # ()
[//]: # (- [ ] [Get started with GitLab CI/CD]&#40;https://docs.gitlab.com/ee/ci/quick_start/index.html&#41;)

[//]: # (- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing&#40;SAST&#41;]&#40;https://docs.gitlab.com/ee/user/application_security/sast/&#41;)

[//]: # (- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy]&#40;https://docs.gitlab.com/ee/topics/autodevops/requirements.html&#41;)

[//]: # (- [ ] [Use pull-based deployments for improved Kubernetes management]&#40;https://docs.gitlab.com/ee/user/clusters/agent/&#41;)

[//]: # (- [ ] [Set up protected environments]&#40;https://docs.gitlab.com/ee/ci/environments/protected_environments.html&#41;)

[//]: # ()
[//]: # (***)

