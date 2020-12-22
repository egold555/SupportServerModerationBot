# Support Server Bots
These are the bots for my support server. You are free to request things you think would benefit the server.

I won't give much support for hosting these yourself. Everything is hard coded and really needs documentation.

# Building
### Updating dependencies
Before building Moderation and Utilities bots, you must refresh the dependencies from Shared. You can do this by running `mvn install` in `/_Shared`.
### Building Moderation
To build the moderation bot you must go into the `/Moderation` directory and run `mvn clean package`. This will then give you a jar in `/Moderation/target`. You can then run the bot using `java -jar ModerationBot-1.0-SNAPSHOT.jar`
### Building Utilities
Building the utilities bot is the same as building the moderation bot. Go into the `/Utilities` directory and run `mvn clean package`. You can then get the jar from `/Utilities/target`

# Running
Instructions for running the bot is coming soon.