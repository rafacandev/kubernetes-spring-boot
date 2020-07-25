Kubernetes Spring Boot Configuration - Quick Start
==================================================
A simple Spring Boot project to demonstrate how to configure Spring Boot applications running in Kubernetes.

Requirements
------------
The following applications should be installed in your local machine:
    * [Git](https://www.atlassian.com/git/tutorials/install-git)
    * Java 11, I suggest [SDKMAN](https://sdkman.io/usage)
    * Maven, I suggest using [SDKMAN](https://sdkman.io/usage)
    * [Docker](https://www.docker.com/get-started) 
    * Kubernetes, I suggest for local development [minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/)

Build a Docker Image
--------------------
Clone our sample application:
```
git clone https://github.com/rafasantos/kubernetes-spring-boot-config-map.git
cd kubernetes-spring-boot-config-map
```

Build the application; our `.jar` file will be located at: `kubernetes-spring-0.0.1-SNAPSHOT.jar`
```
mvn package
```

Build a docker image based on the included [Dockerfile](Dockerfile)
```
docker build -t rafasantos/kubernetes-spring .
```

I also recommend reading [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
for more info about creating Dockerfile for Spring Boot apps.

Run our docker image locally:
```
docker run rafasantos/kubernetes-spring
```

The application outputs a summary of the values we want to configure.
These values are defined in [bootstrap.yml](src/main/resources/bootstrap.yml)
and [bootstrap-qa.yml](src/main/resources/bootstrap-qa.yml)
```
--- Configuration Values ---
spring.application.name: MyApplicationName
hello.public.message: My public message from bootstrap.yml
hello.secret.message: My secret message from bootstrap.yml
```

Spring boot profiles can be set via environment variables, therefore if we set the active profile to `qa` we will see
the values stored in [bootstrap-qa.yml](src/main/resources/bootstrap-qa.yml)

```
docker run --env SPRING_PROFILES_ACTIVE=qa rafasantos/kubernetes-spring
```
```
hello.public.message: My public message from bootstrap-qa.yml
```

Spring boot also allow configuration values via environment variables.
For example, we can set `hello.public.message` with the `HELLO_PUBLIC_MESSAGE` environment variable.
```
docker run --env HELLO_PUBLIC_MESSAGE='My public message from environment variables' rafasantos/kubernetes-spring
```
```
hello.public.message: My public message from environment variables
```

Push Docker Image
-----------------
By default, Kubernetes looks for images in Docker repository.
Therefore we can push our image normally with Docker:
```
docker push rafasantos/kubernetes-spring
```

Alternatively, when using a local Kubernetes cluster (e.g: [Minikube](https://kubernetes.io/docs/setup/learning-environment/minikube/))
then it can be configured to use the images from a local Docker deamon:
```
# Switches the terminal to connect to Minikube's dockers deamon
eval $(minikube docker-env)

# Note that Minikube's docker deamon is not awere of our image; therefore we need to re-create our image in Minikube's docker deamon
docker build -t rafasantos/kubernetes-spring .
```

Build a Kubernetes Pod
----------------------
Build a Kubernetes Pod based on the included [kubernetes-spring.pod.yml](kubernetes-spring.pod.yml) 
```
kubectl apply -f kubernetes-spring.pod.yml
kubectl logs -f pod/kubernetes-spring
```

> Note, if you are using a remote docker repository make sure to delete the `imagePullPolicy: Never`
> line from [kubernetes-spring.pod.yml](kubernetes-spring.pod.yml)

Spring Boot Configuration With Environment Variables
----------------------------------------------------
See [Define Environment Variables for a Container](https://kubernetes.io/docs/tasks/inject-data-application/define-environment-variable-container/)
Uncomment the following to [kubernetes-spring.pod.yml](kubernetes-spring.pod.yml)
```
      env:
      - name: HELLO_PUBLIC_MESSAGE
        value: 'My public message from environment variables'
```

Apply the changes and check the logs: 
```
kubectl apply -f kubernetes-spring.pod.yml
kubectl logs -f pod/kubernetes-spring
```

