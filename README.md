# ScienceOlympiad-Server
The Server for the Science Olympiad Application

## Purpose

##### PSU Capstone Experience
The purpose of this application was to build a Software Product with an agile approach such that we could 
build a functional "production ready" Minimal Viable Product.  The idea was to come up with an idea for a product to build
from scratch; in this case, automating the Science Olympiad process by providing a web application for PSU.  The goal was 
to push ourselves to learn new technologies, see the product through end-to-end, while completing sets of deliverables 
every 2-3 weeks which could then be demoed at the end of each sprint.  We also decided to build out a full CI/CD pipeline
for capturing code changes, automating our test suites, and deploying the latest code to the publicly accessible web.

##### Client Facing UI
For this web application, we also built UI for allowing users of the application to interface with the API and facilitate 
all of their Science Olympiad needs:
  >       https://github.com/sweng500-g5-spring2018/ScienceOlympiad-UI

##### Project Management
For managing this project in an Agile fashion, Jetbrains YouTrack was used for all tracking of work.
  >       https://sweng500-g5.myjetbrains.com/


## Getting Started
(Instructions were made using the Intellij IDE, Run configurations may need to be added/modified depending on IDE.)

1. Create a new project - select a maven project if possible (In intellij this is not needed)
   - The project will use Maven for gathering dependencies and compiling a WAR file for app server deployment
2. Clone the project from git 
   - In Intellij - Click VCS -> Checkout from version control -> Github
   - Enter the proper github repo and your project name
3. If your project name does not match the .iml file extratced from github, it will still run 
4. The mavenbuild should have been cloned and ready to run. If not, set up a custom run configuration with the following maven command line arugments. <br />
    ```clean -Dmaven.clean.failOnError=false install package```
5. The generated .war file will be built and placed in ```<project_name>/target/<war file>```
6. Download a version of tomcat to use, preferably > Tomcat 8
7. Navigate to <tomcat_directory>/bin/ and double click on startup.bat -- This will run your tomcat server under localhost:8080
8. Place the generated .war file from step 5 and place under <tomcat_home>/webapps/.
9. Notice the directory structure will be built and tomcat will deploy your application.


## Setting up MongoDB

A mongoDB instance is configured for this application.
Java spring makes it easy to configure this instance for use. 
 - In the Spring Configuration (Java based) an instance can be set up to use by adding the following bean
   This bean will be initialized when deploying the application to tomcat.
  ``` 
  public @Bean Mongo mongo() throws Exception {
        MongoCredential credential = MongoCredential.createCredential("<username>",
                "<database_name>","<password>".toCharArray());
        ServerAddress serverAddr = new ServerAddress("<server_name>",<port>);

        return new MongoClient(serverAddr, Arrays.asList(credential));
    }
  ```
- In order to actually perform operations on the database (insert,update,delete,etc) a mongoTemplate bean is configured
    ``` 
    public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(<mongo_object_from_above>, <database_name>);
    }
    ```
    
- By registering these two objects as bean they are able to be injected automatically by spring by the use of the annotation @Autowired
      This is a strength of spring known as dependency injection
      ```
      http://www.vogella.com/tutorials/SpringDependencyInjection/article.html
      ```



