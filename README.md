# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2Z0YKAE9VuImgDmMAAwA6AJyY7UCAFdsAYgAWAGYADgAmVxAYP2Q7AAswHQRvQwAlFDskVXM5JAg0TD90zOzKGGAYNBQAdxhvAyhnTERUUgBaAD5yShooAC4YAG0ABQB5MgAVAF0YAHp6ygAdNABvACIFqDRgAFsUNb61mDWAGmPcdWroDgOj0+OUHeAkBFvjgF9MYV6YTtZ2LiUAbrTbbPZve5rC6qK5QG6HY5nNaPZ6vBFrT46KAoYAAaxgACFgBwYOkAI4pbJfHplNp-FpmAYBRyOZbrPbqYB2fYDNYAUSgXn6hOJMGxFLUYAxpk4mDYnG4sD+31EA2xJTAlAAFMUspqoOTKWAAJTUkQqX5dWTyJQqdQDblgACqDS1oN2KFN1sUyjUqktRh0fQAYkhODAXZRvTAdBY6g0wWIsTj8ZHYNUkGA4vHKInyghscS4ygAB56jTe21+37KnqqiMNb1mqiiGtdb5AmAKAs4jgWCZ49B8ksqbAEfLN2n05CMmDMoJstYc1RcnnHAVCgbuvb5wt9mDyXHoaXoDiYSu+9Rt7rmlADNDeBAIZutv4Xu2qPogQuatNuxvaF62hVlefzGH0CgcCSaZNiqFpvsBl6ft+OKago3hZlqwAYXE3pATaSEBuBkEkuhWawXW8F-PKgLCrqpQGmoj5YDRirXh2woggmHpHLyiLHNhWYTBAR5oLxxwfJOSrTq0YADGELKLtu+wwHxkKCXEwmieJdyYoW+KpN4aAHkgO6UEK56IR+7E0nRRkEHsG7QFJ14MhgAyDAArCyMwrMcy6rgc66CtAAxahwajfsQ47GRAABmMDmdAxpHJ8p4ymefgADIQHY-phuUxmlnqYYOJsMBatiYDeFs-oVFUtQaVp6DGk0ng+P40DsNy0TBnAfLSHACgwDlmQFG5zC1tQnYjOM0xzAY6h5GgbLKWc0KwhwnwcQGrGdlxuY8eiSIbdcEJ6SmIokoakouXSXQTUyimrEufqBbyTnCkSJLikaJ6yntSrtpRd4wAguUAJJoFqo1Qzd2SmnBhgIQRH4Oigzqusp+E+tZYFBqG4YwdoMZxspmDJniEbbDh0BIAAXig0GummeYAjAwz6DC1xGPkADkzBPGAIBxDjIH+vdN4tioAzE-IL5UcD03Ck6NNZnTjPM2Uw6jjFd0ybOzIAIyLgF3JBfyIXCkZGka0z-1nu+1aS0j96Ps+SMBk79ooXIKBkXEWE4Xhlmo87XTEVBXbB9ooe487nSu9H5Gx57kuAwMsNoOkqjMXKAJsS7tnAhs3HgqpEnqThzViRXun6w9M7uTACmsi9yk6fxaxNSJx515JlMGUZJlmVbcfizZysDIZGCmSgn0N-ATdyUM3mOL5-lvebH1W2FEWqFFY7LTA8WJVbKUwGlsrpdluX+j4zBZoY5XYtg2IGBgTOkwecSGD3oltS8L4PwngUDoGiLEBIoDwGjQfs0ZeAYOIeWkHyLKfIJh8hmLMRaqhlrLH-ugO6XQM7lGrr3AogNEEgwGODOwowMJalofQsA8MTQK2RlaKyfp0aY21AQtAYtCL4xDGGLWUBoyxmTnEDWuR8gwBrhTfSUiZExQPOQ3mag0AC0SmWKk3sJZTWlqDOWwB2GT16LLNW0ioAMy-mmGAOsUBHwnDtSWj05yOBNi9M2a5LabjqFYu2Nwr6Oy4aBCOQZpAoG4JqKR0ZT5pnHkIwx9YHxPjMenAunZRrMJznnShRcp4wBWNtWyrll4eVbr5C6VMZ4j0MElKASS8aGM7DPBy88x6uINs3LyPlimb05NvYK-jwqRRsc42KCVGkXxCRlQoABxJANA6pgz1CfBKwAnwwDsB6VQgCOp+GxCSPw2Awz4iyushZez4GySoUUwYCz0FYN2XsfBZDRJEP+AqTs-D84-KBlLesyBsjXI5IwvUYK1CsMRiDL2YTPw7Ixn+fhgi8YRJEUTAC8hv78JgDARRl1Va2xsZrBslAzjfSkWYQgsjjIKP0eY+sJiMmtJVoE0ldiGgOJHE4vW3TG6ySel49kW9fGfS3By2xwT5lp04WHe0SKwBQtUFqNF4dAx9HSOYVANB8wIBgCqvmhqPRNkZYUoxDpTWpzhZkgFNDIV7LyQgFiWTpJKwsQMtYry1BBUGOsH1ENpBBSNmEIIAQkTVDiJmFArMjprEhEkUAuI43gkOJCH1AA5D06b3gwCmKU5W5ShVDCqfivy3q9l+oDR6INIaw0RuOFGmNqaeQJqREmkAKay5tozR6bNexc35pqUPYyHSz4WXNWy6e9k54LwFUvEtfT15ep8RbCVFV96H1UafGZqV5k3zgD+NQ5RKg1B2R6A5wCOAAHZXCOBQI4aIfIghwH6gANngMek1O5blmHuZ62akwXkeneUJchbIs0ekLT8aibrgRQb2COq6pIUASipAu9xzI26iqGeK3eKHfqSgdv82iAH6y+01FCrUlGUBQphRk+V8dFWOhRR8lqzSNXgUJmIiRcY-mD2piS6V5KoCUtFAHGlIA6XyPIZx0CKSZaiYorecxnZiW005WInlutlqLyw5402Yr10EZtpp6VJGp0etSe7RjMgEVfmPdR9V4TNUkRgJm89UKYAZizKehqP7DBBvkwYjoScoUqaMdeEhR7UJ0Y9M611AK1OcWOIh3tl99MVNLf0itgbg0DFDeGzLgm6njsaSFlLM7Z6OS6WUtx2Xl0b1enhkzoyt0TJ3dM8++70o3wAGqUCQHFVA-osxyB-oYawKAiAja-j6nR5ZCokmJBwMbv8YDSafGUfQk2xRoaNF-EAEBwawEgHtn1V7-AWGieDWoMQkDxDADdp8EBagACkIAFW834TtuI-3tA9TNYYTp5qzB9WBzSEHVjYAQMAG7UA4AnegGcfLMHaTEPg8U2H8PKBI9O6j2t0hkNUtYVlkt2GjOtZ3v4qlRHsgkYKdZpTAArL7aBqOfbDPRg7kpYWqZRsxxFrHXSosq8InjymSaSIE0ojT6stOifE6RHCUmZMMoRUypTLK07TqE+Zsl9jHGTPJ4bQz3jjM09CgE4TmtLOa4l6IwLfHAvBcE95rZe44xxXdrMfH0ADyDgKFZoFSmIs2oF3r4M7sL17D98js7QedN8r05hxr85mtrqt8KT3vY4yHj7nMkPSc0ke1tUx8WfQ2fc49P+Q6ex1pw4R-7qALmJYYvczjhHajY9iF15j+1MAucc4S0xF1pHC565Kab5uABIQYZbp+leHuVsexfi6klnbViyael1r0z5bkZ1uxkH068fXdPXMt9dlH4RAOIoDrcMFoeQegDDOFQzsCAKz9U5gf4TjkZw7M-C+yHgQC-gng8OECj2UCUAkBd+eA5Q2AsOtKqiAOk0QOwogwKCaCGCWCxgXyGc0+TOoeoMEUMSKAWoUS5BDGcq9mCqiKZBGMFBbeREQYS89+j+Rg2gr+KA7+VBTBdU2yv+gBOEgB4YPqkWr4cGg+-BmoiWE+U4GBwI6O7qi6s4C+uWyGZWc8E6zk6+RS7Sc6dWRaDWe+uWgyK4wyfix+HW0U5+3WQosyB6nAQAA

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
