# TheAppengers

Our Project: Collaborative Whiteboard 

Team Members: Rahul Bangre, Harsh Dave, Reyansh Patange, Armeen Talwandi

[Project Description](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Project-Description)

[Meeting Minutes: They are ordered by date.](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Meeting-Minutes)

[Requirements](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Requirements)

[Design](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Design)

[Discussion](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Discussion)

[Getting Started](https://git.uwaterloo.ca/atalwand/theappengers/-/wikis/Getting-Started)

## Installation Guide
For your convenience, we have provided 3 methods of installing and running the various.  
There are two ways of running the application locally, the server can be run in a docker container or through the terminal.  

[Latest Release - 2.0.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/2.0.0)

[Release 1.2.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/1.2.0)

[Release 1.1.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/1.1.0)

[Release 1.0.0](https://git.uwaterloo.ca/atalwand/theappengers/-/releases/1.0.0)


### Installing the Cloud Deployed version of the Appenger's Whiteboard (Recommended)

Everything you need to install any version of this application can be found here: https://git.uwaterloo.ca/atalwand/theappengers/-/tree/main/releases?ref_type=heads

The first and simplest option to use the Appenger's Whiteboard is by simply navigating to
`./releases/final/remote/` **from the root of this repository**. Here you will find `AppengersWhiteboard_REMOTE.dmg` installer for `macOS`. This version of the client has
been configured to connect to our Google Cloud Platform instance of our server and our database hosted on Google Cloud SQL. 
Once you install this, you are good to go to use the cloud deployed version of the Appenger's Whiteboard.

##### Notes: 
- Avoid drawing too many strokes on whiteboard too quickly. Due to limitations put on our server by GCP, if too many requests are sent at once from the client, the connection might be refused by the server causing the app to crash. If this happens, please restart the app and continue using as normal.
- Performance might be a little slow or laggy while using the remote deployed version of the app due to the fact that it is deployed on the cloud on a budget-friendly instance. It might be a little slow especially during start up. 

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

