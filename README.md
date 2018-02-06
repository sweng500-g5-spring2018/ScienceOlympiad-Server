# ScienceOlympiad-Server
The Server for the Science Olympiad Application

Getting Started 
(Instructions were made using the Intellij IDE, Run configurations may need to be added/modified depending on IDE.)

1. Create a new project - select a maven project if possible (In intellij this is not needed)
2. Clone the project from git 
   - In Intellij - Click VCS -> Checkout from version control -> Github
   - Enter the proper github repo and your project name
  
3. If your project name does not match the .iml file extratced from github, it will still run 
  
4. The mavenbuild should have been cloned and ready to run. If not, set up a custom run configuration with the following maven command line arugments.
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
  
- In order to actually perform operations on the database (insert,update,delete,etc) a mongoTemplate bean is configured
     
       public @Bean
    MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(<mongo_object_from_above>, <database_name>);
    }
    ```
    
- By registering these two objects as bean they are able to be injected automatically by spring by the use of the annotation @Autowired
      This is a strength of spring known as dependency injection --                        ```http://www.vogella.com/tutorials/SpringDependencyInjection/article.html```



