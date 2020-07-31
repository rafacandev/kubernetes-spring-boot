Kubernetes Spring Boot External Logback Configuration
=====================================================
A simple Spring Boot project to demonstrate how to configure Spring Boot with external logging configuration
which can be updated at runtime in Kubernetes.

Requirements
------------
The following applications should be installed in your local machine:
* [Git](https://www.atlassian.com/git/tutorials/install-git)
* Java 11, I suggest [SDKMAN](https://sdkman.io/usage)
* Maven, I suggest using [SDKMAN](https://sdkman.io/usage)
* [Docker](https://www.docker.com/get-started) 
* Kubernetes, I suggest [Install Minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/) for local development

Build a Docker Image
--------------------
Clone our sample application:
```
git clone https://github.com/rafasantos/kubernetes-spring-boot.git
cd kubernetes-spring-boot/sample-logging
```

Build the application; our `.jar` file will be located at: `kubernetes-spring-0.0.1-SNAPSHOT.jar`
```
mvn package
```

Set the environment variable `LOGGING_CONFIG` which tells Spring Boot to use logging configuration from an external file:
```
export LOGGING_CONFIG=src/main/resources/logback.xml
```

Run the application locally:
```
java -jar target/kubernetes-spring-logging-0.0.1-SNAPSHOT.jar 
```

Our application outputs the content of the logging configuration file and logs a `INFO` message every 2 seconds:  
```
Found environment variable LOGGING_CONFIG=src/main/resources/logback.xml
Loading logging config content
<configuration scan="true" scanPeriod="5 seconds">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
...
13:26:42.927 INFO  c.g.r.logging.LoggingScheduler - Logging message at INFO level
13:26:44.927 INFO  c.g.r.logging.LoggingScheduler - Logging message at INFO level
``` 

If we change the content of `src/main/resources/logback.xml` and replace the `<root level="INFO">` with `<root level="DEBUG">`
we will see the log message applying the new configuration values at runtime:
```
13:32:18.597 INFO  c.g.r.logging.LoggingScheduler - Logging message at INFO level
13:32:18.598 DEBUG c.g.r.logging.LoggingScheduler - Logging message at DEBUG level
```

Build a docker image based on the included [Dockerfile]. I also recommend reading [Spring Boot Docker] for more information.
```
docker build -t rafasantos/kubernetes-spring-logging:v1 .
```

Publish Docker Image
--------------------
By default, Kubernetes looks for images in Docker repository. Therefore, we can push our image normally:
```
docker push rafasantos/kubernetes-spring
```

Alternatively, when using a local Kubernetes (e.g: [Minikube]);
then, it can be configured to use the images from the local Docker deamon:
```
# Switches the terminal to connect to Minikube's docker deamon
eval $(minikube docker-env)

# Minikube's docker deamon runs separetely from the main docker deamon
# Therefore, we need to re-create our image in Minikube's docker deamon
docker build -t rafasantos/kubernetes-spring-logging:v1 .
```

Kubernetes Deployment
=====================
Apply the Kubernetes descriptor [deployment.yml]:
```
kubectl apply -f deployment.yml
kubectl logs -f kubernetes-spring-logging
```

As before, we are going to see log messages at the INFO level:
```
17:52:19.436 INFO  c.g.r.logging.LoggingScheduler - Logging message at INFO level
```

If we change the content of `deployment.yaml` and replace the `<root level="INFO">` with `<root level="DEBUG">`
we will see the log message applying the new configuration values at runtime:
```
# In another terminal
sed -i 's/level="INFO/level="DEBUG/g' deployment.yml 
kubectl apply -f deployment.yml
```

Configuration Break Down
------------------------
By default, when an environment variable `LOGGING_CONFIG` is present; then, Spring Boot will apply the logging configuration from the file.
As described in [Custom Log Configuration].
Therefore, we set `LOGGING_CONFIG=src/main/resources/logback.xml` to use the content of that file.

Our `LOGGING_CONFIG=src/main/resources/logback.xml` is configured to [Automatically reloading configuration file upon modification].
Therefore, our application is capable to update logging configuration at runtime.
```
<configuration scan="true" scanPeriod="5 seconds">
...
```

The [deployment.yml] contains an `metadata.annotation` with the embedded value we want to use as `logback.xml`:
```
metadata:
  name: kubernetes-spring-logging
  annotations:
    logback: |-
      <configuration scan="true" scanPeriod="5 seconds">
          <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
              <encoder>
                  <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
              </encoder>
          </appender>
          <root level="INFO">
              <appender-ref ref="STDOUT" />
          </root>
      </configuration>
```

The `deployment.yml` also contains the environment variable `LOGGING_CONFIG` with the value `/etc/config/logback.xml`:
```
spec:
  containers:
    - name: kubernetes-spring-logging
      image: rafasantos/kubernetes-spring-logging:v1
      env:
        - name: LOGGING_CONFIG
          value: /etc/config/logback.xml
```

The [deployment.yml] also contains a volume that mounts the content of `metadata.annotations['logback']`
as a file located at `/etc/config/logback.xml`:
```
spec:
  containers:
      ...
      volumeMounts:
        - name: logback
          mountPath: /etc/config
  volumes:
    - name: logback
      downwardAPI:
        items:
          - path: logback.xml
            fieldRef:
              fieldPath: metadata.annotations['logback']
```

Therefore, when we change the `metadata.annotation.logback` value in our [deployment.yml] and ask Kubernetes to apply the
new configuration description with `kubectl apply -f deployment.yml`; results in the `/etc/config/logback.xml` being update
which cause Logback to detect the changes and apply the new logging configuration at runtime.

References
==========
[Custom Log Configuration]
[Minikube]

[Automatically reloading configuration file upon modification]: http://logback.qos.ch/manual/configuration.html#autoScan

[Custom Log Configuration]: https://docs.spring.io/spring-boot/docs/2.1.x/reference/html/boot-features-logging.html#boot-features-custom-log-configuration
[Minikube]: https://kubernetes.io/docs/setup/learning-environment/minikube/
[Spring Boot Docker]: https://spring.io/guides/gs/spring-boot-docker/
[Dockerfile]: Dockerfile
[deployment.yml]: deployment.yml
