<!-- vscode-markdown-toc -->
* [Dependencies](#Dependencies)
* [Properties](#Properties)
* [Creating the Commands & Events](#CreatingtheCommandsEvents)
	* [Commands](#Commands)
	* [Lets create some controllers](#Letscreatesomecontrollers)
		* [Commands Controllers](#CommandsControllers)
		* [testing our App](#testingourApp)
		* [testing with http](#testingwithhttp)
	* [Adding events](#Addingevents)
	* [Adding Aggregate](#AddingAggregate)

<!-- vscode-markdown-toc-config
	numbering=false
	autoSave=true
	/vscode-markdown-toc-config -->
<!-- /vscode-markdown-toc -->


# event_driven_architecture_cqrs_and_event_sourcing

# Objectif 

> Create an application that allows for managing accounts in accordance with the CQRS and Event 
> Sourcing patterns using the AXON and Spring Boot frameworks

# POC (Proof of concept)

- Axon framework : https://docs.axoniq.io/reference-guide/v/4.0/

# Practical Demo

![](./images/1.PNG)

## <a name='Dependencies'></a>Dependencies 

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

## <a name='Properties'></a>Properties 

```properties
spring.application.name= account-service
server.port= 8081

spring.datasource.url= jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/accounts?createDatabaseIfNotExist=true
spring.datasource.username=${MYSQL_USERNAME:root}
spring.datasource.password=${MYSQL_PASSWORD:}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.MariaDBDialect
```

- We used `Env` variables like `MYSQL_HOST` to make it easear for us to inject them in the `Docker compose` file.
- In this case spring well use the variable value if it is defined, and the default value `localhost` if not. 


## <a name='CreatingtheCommandsEvents'></a>Creating the Commands & Events

![](./images/2.PNG)

- The events and commands are common between all the app micro-services.
- So as they are common, we can create them in a separated module. to be used from anywhere.
- In this case will not do that, we will just use packages because we have 1 micro-service.

### <a name='Commands'></a>Commands
- Hold the functional need of the application (Besoin fonctionel).

- BaseCommand
```Java
package me.elaamiri.accountcqrseventsourcing.common_api.commands;

public abstract class BaseCommand<IDType> {
    @TargetAggregateIdentifier
    // the command ID is the identifier that will be used in the
    // Aggregate

    @Getter // because the commands are immutable objects
    // So we will not have the setters
    // Just a constructor for initializing
    private IDType id;

    public BaseCommand(IDType id) {
        this.id = id;
    }   
}
```

- CreateAccountCommand
```Java
package me.elaamiri.accountcqrseventsourcing.common_api.commands;

public class CreateAccountCommand extends BaseCommand<String>{

    private double initialBalance;
    private String currency;

    public CreateAccountCommand(String id, double initialBalance, String currency) {
        super(id);
        this.initialBalance = initialBalance;
        this.currency = currency;
    }

}

```

- DebitAccountCommand
```Java
package me.elaamiri.accountcqrseventsourcing.common_api.commands;

public class DebitAccountCommand extends BaseCommand<String>{

    private double amount;
    private String currency;

    public DebitAccountCommand(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}


```

- CreditAccountCommand
```Java
package me.elaamiri.accountcqrseventsourcing.common_api.commands;

public class CreditAccountCommand extends BaseCommand<String>{
    private double amount;
    private String currency;

    public CreditAccountCommand(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}

```

### <a name='Letscreatesomecontrollers'></a>Lets create some controllers

- Commands and query, should have Controllers both.
- So we will have controllers for the reading part (Query), and others for writing (Commands).

#### <a name='CommandsControllers'></a>Commands Controllers

- AccountCommandController
```java
package me.elaamiri.accountcqrseventsourcing.common_api.controllers;
// ...
@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor
public class AccountCommandController {

    private CommandGateway commandGateway;

    @RequestMapping("/create")
    public CompletableFuture<String> createAccount(@RequestBody CreatAccountRequestDTO request){
        //
        CompletableFuture<String> createAccountCommandResponse = commandGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.getInitialBalance(),
                request.getCurrency()
        ));

        return createAccountCommandResponse;
    }
}

```
- 

```java
package me.elaamiri.accountcqrseventsourcing.common_api.dtos;
// ...
@Data @AllArgsConstructor @NoArgsConstructor
public class CreatAccountRequestDTO {
    private double initialBalance;
    private String currency;

}

```

#### <a name='testingourApp'></a>testing our App

- Run our App

- Output

```yaml
***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in me.elaamiri.accountcqrseventsourcing.common_api.controllers.AccountCommandController required a bean of type 'org.axonframework.commandhandling.gateway.CommandGateway' that could not be found.


Action:

Consider defining a bean of type 'org.axonframework.commandhandling.gateway.CommandGateway' in your configuration.


Process finished with exit code 1

```

- If There anther exception, check if it is related to the versions compatibility between `Spring` and `Axon`.

- **Solution**: :fire: adding `@NoArgsConstructor` (public default constructor) with `@AllArgsConstructor` in top of the class. :x:Will discover that it is just a temporary solution that produces another problem -> NullPointerException:x:

- **Result**: an empty database 
- **Expected**: have Axon related tables in the DB.
- **Problem**: Axon Framework doesn't work with Spring Boot 3 yet. 
- **Solution**: use `Spring 2.7.6` with `Axon 4.6.2`
- Here my DB 

![](./images/3.PNG)

#### <a name='testingwithhttp'></a>testing with http

```http
POST /commands/account/create HTTP/1.1
Host: localhost:8081
Content-Type: application/json
Content-Length: 61

{
    "initialBalance": 1500.2,
    "currency" : "MAD"

}
```

- Response

```res
HTTP/1.1 500 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 19 Dec 2022 22:34:27 GMT
Connection: close

{
  "timestamp": "2022-12-19T22:34:27.121+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/commands/account/create"
}
```

- **Exception**

```yml
java.lang.NullPointerException: Cannot invoke "org.axonframework.commandhandling.gateway.CommandGateway.send(Object)" because "this.commandGateway" is null
```
- The problem is that we do not have a handler : `No handler was subscribed to command ... CreateAcountCommand`.

- To get thing more clear we will add an exceptionHandler method to our Controller.


```java
@ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception){
        ResponseEntity<String> responseEntity = new ResponseEntity<>(
            exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR
        );

        return responseEntity;
    }
```


- :fire: In fact the last Exception it was because I used the  `@NoArgsConstructor` (public default constructor).
- Now after removing it :

```yml
adding `@NoArgsConstructor` (public default constructor)
```

### <a name='Addingevents'></a>Adding events

```java
package me.elaamiri.accountcqrseventsourcing.common_api.events;

public abstract class BaseEvent<EventId> {
    @Getter
    private EventId id;

    public BaseEvent(EventId id){
        this.id = id;
    }

}

```

- AccountCreatedEvent

```java
package me.elaamiri.accountcqrseventsourcing.common_api.events;

public class AccountCreatedEvent extends BaseEvent<String>{

    @Getter
    private double initialBalance;
    @Getter
    private String currency;

    public AccountCreatedEvent(String id, double initialBalance, String currency) {
        super(id);
        this.initialBalance = initialBalance;
        this.currency = currency;
    }
}

```

### <a name='AddingAggregate'></a>Adding Aggregate

- In the Aggregate where we put our Business code.
- In the Aggregate we will create the CommandHandler.
- It is the status of our Object

```java
package me.elaamiri.accountcqrseventsourcing.commands.aggregates;

@Aggregate
//@NoArgsConstructor // Important
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId; // identifies the aggregation
    // This id will be mapped to the TargetAggregateIdentifier in the baseCommand
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate(){
        // Required by Axon
    }

    @CommandHandler // Subscribe to Command Bus, and listen to the CreateAccountCommand events
    public AccountAggregate(CreateAccountCommand createAccountCommand){
        // Business logic

        if(createAccountCommand.getInitialBalance() < 0) throw new RuntimeException("Invalid Initial Balance | Negative");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                // Command to event
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency()
        ));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent accountCreatedEvent){

        // The Aggregate is the Object Status
        this.accountId = accountCreatedEvent.getId();
        this.balance = accountCreatedEvent.getInitialBalance();
        this.currency = accountCreatedEvent.getCurrency();

        this.status = AccountStatus.CREATED;
    }

}

```

- Test

- Request 

```http
POST /commands/account/create HTTP/1.1
Host: localhost:8081
Content-Type: application/json
Content-Length: 61

{
    "initialBalance": 1500.2,
    "currency" : "MAD"

}
```

- Response 

```res
HTTP/1.1 200 
Content-Type: text/plain;charset=UTF-8
Content-Length: 36
Date: Mon, 19 Dec 2022 23:56:00 GMT
Connection: close

3696ac91-6749-482a-bb5e-16999e911758

```
- Account Created .
- We can see the 2 events in the `Event store` which is  the  `domain_event_entry` in the DB.

![](./images/4.PNG)


1.15