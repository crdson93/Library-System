# Use an official Scala image as a base
FROM hseeberger/scala-sbt:11.0.11_1.5.5_2.13.6

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . /app

# Update sbt and build the project
RUN sbt update
RUN sbt compile

# Define the command to run the application
CMD ["sbt", "run"]
