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
    * Kubernetes, I suggest [Install Minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/) for local development

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

Run the application locally:
```
java -jar target/kubernetes-spring-0.0.1-SNAPSHOT.jar 
```

The application outputs the configuration values that later we are going to configure in Kubernetes.
These values are initially defined in [bootstrap.yml] and [application.yml].
```
--- Configuration Values ---
spring.application.name: kubernetes-spring
hello.message.environment.variable: Placeholder, environment message from application.yml
hello.message.public: Placeholder, public message from application.yml
hello.message.secret: Placeholder, SECRET message from application.yml
``` 

Build a docker image based on the included [Dockerfile]. I also recommend reading [Spring Boot Docker] for more information.
```
docker build -t rafasantos/kubernetes-spring:v1 .
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
docker build -t rafasantos/kubernetes-spring .
```

Spring Boot With ConfigMap and Secrets
======================================
Apply the Kubernetes descriptor `deployment.yml`:
```
kubectl apply -f deployment.yml
kubectl logs -f kubernetes-spring
```

The application outputs the configuration values sourced in the `deployment.yml`.
```
--- Configuration Values ---
spring.application.name: kubernetes-spring
hello.message.environment.variable: Placeholder, environment message from application.yml
hello.message.public: Placeholder, public message from application.yml
hello.message.secret: Placeholder, SECRET message from application.yml
```

Configuration Break Down
------------------------
In our Spring Boot application we needed to define a few properties (using property format instead of yaml for shortness).
**bootstrap.yml**
```
# Disable configuration client which tells Spring Boot to not attempt to find a configuration server at start-up
spring.cloud.config.enabled=false
# Enable secrets API which tells Spring Boot to load secrets from Kubernetes API
spring.clound.kubernetes.secrets.enableApi: true
```
**application.yml**
```
# Spring Boot uses the application name as default configuration name objects (e.g: ConfigMap and Secret names)
spring.application.name: kubernetes-spring
```

In our `deployment.yml` we had to define a number of Kubernetes objects.

**Role** and **RoleBinding** are necessary; so, Spring Boot is able to read ConfigMap and Secret objects from Kubernetes API.
See [[Spring Cloud Kubernetes] - Service Account](https://cloud.spring.io/spring-cloud-kubernetes/reference/html/#service-account)
**bootstrap.yml**
```
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
...

---

kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
...
```

**Secret** contains the secrets for our application:
**bootstrap.yml**
```
kind: Secret
apiVersion: v1
...
data:
  hello.message.secret: SXQgd29ya2VkISBTZWUgW1NlY3JldF0uZGF0YS5oZWxsby5tZXNzYWdlLnNlY3JldCBpbiBrdWJlcm5ldGVzLXNwcmluZy55bWwK
```


**ConfigMap** describes the plain configuration values for our application.
In this example, we expose values from an embedded `application.yml` for simplicity; nevertheless, key value pairs are also valid: 
**bootstrap.yml**
```
kind: ConfigMap
apiVersion: v1
metadata:
  name: kubernetes-spring
data:
  application.yml: |-
    hello:
      message:
        public: It worked! See [ConfigMap].data.application.yml in kubernetes-spring.yml
```

**Pod** describes the container for our Docker image.
```
kind: Pod
apiVersion: v1
metadata:
  name: kubernetes-spring
spec:
  containers:
    - name: kubernetes-spring
      image: rafasantos/kubernetes-spring:v1
      env:
        - name: HELLO_MESSAGE_ENVIRONMENT_VARIABLE
          value: It worked! See [Pod].spec.containers.env in deployment.yml
```


References
==========
[Spring Cloud Kubernetes](https://cloud.spring.io/spring-cloud-kubernetes/reference/html/)

[Kubernetes Secrets Best Practices]

[Minikube]

[Kubernetes Secrets Best Practices]: https://kubernetes.io/docs/concepts/configuration/secret/#best-practices
[Minikube]: https://kubernetes.io/docs/setup/learning-environment/minikube/
[Spring Boot Docker]: https://spring.io/guides/gs/spring-boot-docker/
[application.yml]: src/main/resources/application.yml
[bootstrap.yml]: src/main/resources/bootstrap.yml
[Dockerfile]: Dockerfile
[deployment.yml]: deployment.yml
