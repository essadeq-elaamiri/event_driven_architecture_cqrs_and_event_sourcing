# event_driven_architecture_cqrs_and_event_sourcing

# Objectif 

> Create an application that allows for managing accounts in accordance with the CQRS and Event 
> Sourcing patterns using the AXON and Spring Boot frameworks

# POC (Proof of concept)

- Axon framework : https://docs.axoniq.io/reference-guide/v/4.0/

# Practical Demo

![](./images/1.PNG)

## Dependencies 

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

```

- Axon framework dependency 

```xml
<!-- axon framework -->
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
    <version>4.6.1</version>
    <exclusions>
        <exclusion>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-server-connector</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

- Why `exclusion` ?

<pre>
Since Maven resolves dependencies transitively, it is possible for unwanted dependencies to be 
included in your project's classpath. For example, a certain older jar may have security issues or 
be incompatible with the Java version you're using. To address this, Maven allows you to exclude 
specific dependencies. Exclusions are set on a specific dependency in your POM, and are targeted 
at a specific groupId and artifactId. When you build your project, that artifact will not be added 
to your project's classpath by way of the dependency in which the exclusion was declared.
</pre>
- https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html#how-to-use-dependency-exclusions 

- In this case we do not want `axon-server-connector`, by default we will use inMemory Axon server, we will not use a broker in this test or a server, that is why we excluded the server_connector.
- So our application will not search for a server to connect. it will use inMemory connection Bus.

- Axon arrives with a buitin broker.

## Creating the Commands & Events

![](./images/2.PNG)

- The events and commands are common between all the app micro-services.
- So as they are common, we can create them in a separated module. to be used from anywhere.
- In this case will not do that, we will just use packages because we have 1 micro-service.

### Commands

- 

```Java

```

```Java

```

```Java

```

```Java

```

```Java

```