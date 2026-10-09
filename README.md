# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+uB5afJCIJqTsezQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3l5d5LsKYoSm6MpymW7xKpgKrBhqbpGBAahoAA5MwVpojA0AwOKIDQCi4AwMZAKBbywWWdZPZ9tuHmWf6zLTJe0BIAAXigHBRjGcaFFpSaVKJaZOAAjAROaqHm8zQUWJb1D4zV6q1HW7HRTbuYKw78mOE4oM+8TnpeVHoHOQUOiFK5rgGl6vs69WVDppaOeKGSqABmCPSB1S6cs-l7N8FHHchixLJp33wsmyCpjAuH4aMhH6fMJGoQDR2IfWyO0Y2DGeN4fj+F4KDoDEcSJITxOOb4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMgPo314PaeZ-q80hH1C-Cvp3fUtn2FTDlCVTzlqK5dWbbe20wIyYD7Yd8F86dFXnZUy5hU+N3aLK8oi+gcWqhqACSaBUCaSDrtbhRbYuVUujANX9htA0djUrqLfEy2dd1KCxgp6EQ4NUPYTA6ZjfDE1TQWYyzdA82h+Hq3Y6rnmG-SHAoNwx6XrrlH6+VC4ChdGQzBANDXS+W7GO2nujjAb37rX+4+u59S9yOhcfuL9SU6er3vZ9gd0zAIxgxJcfwAnYA4XhWZrTjTH4yi67+Ng4oavx2UAOJKhoNMNaWDTnyz7P2EqPNo6L-UPRPxpvzbc+S520s0SXxzA5IBV8lYkkLlSdW9JNZMh1u7A2dd7yhXFGbNu8hLbfz1qLeK6oYCO2dsgN2P8PYwPrt7Lsvtez+zul9FeIcKJ50jtHeM-VIYlA3knUa41+TpxmsWbOCpc5QHap1Bs9EoEyHIbUZAORgFqAxEggeF1TaVgQDABRHoO6By7rULRFsz45H7pVf+VkXQGMwUYrAAdx6wn9NYhRqgZ4IEAuLehwdF7LGfjmAsDRxg+JQPbaQBYRrhGCIEEEmx4i6hQG6Tk-0QTJFAGqeJkEQYgkCQAOSVCDC4nRl6Jg4dDWGWZvFXz8QEpUwTQnhMicsaJsS0kGTGN8ZJIBUlEWmq0zJSoclzEWPknejE8YBA4AAdjcE4FATgYgRmCHALiAA2eAu1NEViKOvWm4N6atA6E-F+ocgZZmyUqQpGE7FfnqO7NYIxTlzDMvYiWQdrKHjkCgBRGI4C7QURAlWAdoFnVgVrBBpDlHBWNqgiU+1IpW1IZoHQncZEwotrlP2tsEoEKdi7EhOCbZdw8dVGhY8zGNREWIrq0Yo69Vjphdew0U7Zj4fmARc1hFMNEStCR606FFzrjtI8HylQYnuQFAlF1HzrLmNoxFuiZE9x8H3cVlCGTVOkCSh4Tz6jfMFb8-8rixZPI8T9MYgSan1DCREmA5zV5DRhlvUY5S5jmpgJawI1qC642Yv4SwZdbKbBJkgBIYBfV9ggAGgAUhAcUUrDD+HaWqTZnDtkMMaE0ZkMkeiBNfnixSoxsAIGAL6qAcAIC2SgGsM10gbWCy1dg6uSFbkFqLZQUt5bK1qseV+FN5iqEACto1oCcRiKN4onF-LcrywFxdu4gsrogkxRshSijQa3K8FsoruwRUioFo4UWYLRcSvBDtsXEPrUDRdXszFEtqrY0lpYmocopSwml7D46cIZbw3MLLCyCNLAtJ9XLhkAukbu2oWtPlVvBUuk2q7LHACwVW7dcqwPwZyrAdFd7NVXNWbq8B+qsDHtjclVKGUYBZWMcq69PtMO8tvvUAAQiGP5L6Y5vrXh+7hcMmXfumr+tleh1wokJMrblDEiNNELcWmABVy2VjNDWcMZDd2Ep9s26Tsncq1htlhhegYFNhiQqxthAtA52vTJmVOzK+OZz-dcqs5oYDaeQsBqdoGZ3zWwFodEkG1VrH8qpKTray3QGg4uSFrovPvNjTKndHmFV9yw49eoo6h34begaue9HF41rM-S+13Hhlevxl4Itgbg2lflIgYMsBgDYALYQPIBQYBJvMD2ySjNmas3ZsYUwH9sNwnqNIMuTJ0goBE8Yv+LyfYgG4HgJRUi9Eybm1ABb4qIuN2boYE0GiIraDWPu4AaxHgxfbrKvl3kEujyo9NqhI9+QauS2vGrLi3FGvnjsnLtK8ucdKaMYZQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
