# BYTE-BUDDY-AGENT

This is a maven project to create an instrumentation agent using [bytebuddy](https://bytebuddy.net/#/) lib.

This agent instruments the source code methods to get the list of test methods executing a particular source-method.
It generates a json file in path of your application `src/main/resources/output.json` which contains a list with source-method name as key
and list of test-methods names (which executes that source-method) mapped to that source-method as values




#### Creating Agent :
Run in terminal : `mvn clean package`

This will generate an executable jar with dependencies in the `target` folder named as 
`byte-buddy-agent-1.0-SNAPSHOT-jar-with-dependencies.jar`.
Use this jar in -javaagent for instrumenting.


#### Using Agent :
In your application pom.xml add this geenrated jar path to your surefire plugin as shown in the below example :
```
<plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-surefire-plugin</artifactId>
   <version>2.22.0</version>
   <dependencies>
     <dependency>
       <groupId>org.junit.jupiter</groupId>
       <artifactId>junit-jupiter-engine</artifactId>
       <version>${junit.jupiter.version}</version>
     </dependency>
   </dependencies>
   <configuration>
     <argLine>
       -javaagent:{your-project-path}/byte-buddy-agent/target/byte-buddy-agent-1.0-SNAPSHOT-jar-with-dependencies.jar
     </argLine>
   </configuration>
</plugin>              
```
