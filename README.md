# Solution

- Java client chosen: 


Homework Development Task
Java Developer, Cloud Adapter
2020
 

Prerequisite:
- Have a Docker instance setup
 
Write a Spring Boot (or similar framework) Java application that:
- Connects to Docker REST API directly (we’d like to see your implementation of a simple REST client in Java, not using SDK)
- Pulls an Ubuntu image
- Runs the container in the background and waits for it to be ready
- Executes Linux commands inside the container to (a) 
print out CPU and memory usage and (b) 
capture them for showing in a simple HTML page
- Serves these CPU and memory values in the HTML page where they are automatically refreshed
- This page has a “Destroy” button and when it is pressed, container is destroyed
 
Note:
- An acceptance test and README.md should be included with the program
- If you run the program again, it should use image that has been already downloaded
 
Packaging:
- Create a Dockerfile which would create a container with your application and one could execute it with "docker run" command