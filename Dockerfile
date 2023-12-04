# Use an official Amazon Corretto runtime as a parent image
FROM amazoncorretto:17

# Install SQLite
RUN yum update -y && yum install -y sqlite

# Set the working directory to /app
WORKDIR /app

# Copy the shadow JAR file into the container
COPY releases/final/docker-local/server-all-docker-local.jar /app/server-all.jar

# Set a volume for the SQLite database to ensure data persists
VOLUME /app

# Specify the default command to run your service
CMD ["java", "-jar", "server-all.jar"]
