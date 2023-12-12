# BYTE-BUDDY-AGENT

This is a maven project to create an instrumentation agent using [bytebuddy](https://bytebuddy.net/#/) lib.

This agent instruments the source code methods to get the list of test methods executing a particular source-method.
It generates a json file in application path `src/main/resources/source-tests-map.json` which contains a list with 
source-method name as key and list of test-methods names (which executes that source-method) mapped to that 
source-method as values.



#### Creating Agent :
Run in terminal : `mvn clean package`

This will generate an executable jar with dependencies in the agent's `target` folder named as
`byte-buddy-agent-1.0-SNAPSHOT-jar-with-dependencies.jar`.
Use this jar in -javaagent for instrumenting.


#### Using Agent :
In your application pom.xml add this generated jar path to your surefire plugin as shown in the below example :

```
 <properties>
    <bytebuddy.agent.path>
        /Users/jaspreetsingh/IdeaProjects/byte-buddy-agent/target/byte-buddy-agent-1.0-SNAPSHOT-jar-with-dependencies.jar
    </bytebuddy.agent.path>
 </properties>

 <plugins>
     <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-surefire-plugin</artifactId>
         <version>2.22.0</version>
         <configuration>
             <argLine>-javaagent:${bytebuddy.agent.path}</argLine>
         </configuration>
     </plugin>
 </plugins>          
```
