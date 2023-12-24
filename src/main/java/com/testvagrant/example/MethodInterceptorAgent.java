package com.testvagrant.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodInterceptorAgent {

    public static Map<String, List<String>> methodTestMap = new HashMap<>();
    public static Logger log = LoggerFactory.getLogger(MethodInterceptorAgent.class);

    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("Starting the byte-buddy-agent");

        new AgentBuilder.Default()
                .type(ElementMatchers.nameContains("com.testvagrant.example.implementation"))
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                        builder.visit(Advice.to(MyInterceptor.class)
                                .on(ElementMatchers.isMethod()
                                        .and(ElementMatchers.not(ElementMatchers.nameContains("<init>")))
                                        .and(ElementMatchers.not(ElementMatchers.nameContains("<clinit>")))
                                )
                        )
                )
                .installOn(inst);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveMethodTestMapToJsonFile("src/main/resources/source-tests-map.json");
            log.info("Shutdown hook executed. json file generated at - src/main/resources/source-tests-map.json");
        }));
    }

    public static void saveMethodTestMapToJsonFile(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File(filePath), methodTestMap);
        } catch (IOException e) {
            log.error("Failed to add output data to json : " + e.getMessage());
        }
    }

    public class MyInterceptor {
        @Advice.OnMethodEnter
        public static void enter() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String methodName = stackTrace[1].getClassName() + "." + stackTrace[1].getMethodName();
            String callingClass = stackTrace[2].getClassName();
            String callingMethodName = callingClass + "." + stackTrace[2].getMethodName();
            if ((callingClass.endsWith("Test")) || (methodTestMap.get(callingMethodName) != null)) {
                String testName;

                if (callingClass.endsWith("Test")) {
                    testName = callingMethodName;
                } else {
                    testName = methodTestMap.get(callingMethodName).get(methodTestMap.get(callingMethodName).size() - 1);
                }

                List<String> testNamesMapped;
                if (methodTestMap.get(methodName) == null) {
                    testNamesMapped = new ArrayList<>();
                    methodTestMap.put(methodName, testNamesMapped);
                }
                methodTestMap.get(methodName).add(testName);
            }
        }
    }
}