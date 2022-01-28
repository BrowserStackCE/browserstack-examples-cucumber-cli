<h1 align="center">   :zap: <img src="https://avatars.githubusercontent.com/u/1119453?s=200&v=4" width="60" height="60" > <a href="https://github.com/browserstackce/browserstack-examples-cucumber-cli">BrowserStack Examples Cucumber CLI</a>  <img src="https://avatars.githubusercontent.com/u/320565?s=200&v=4" width="60" height="60" >
 :zap:</h1>

Welcome to BrowserStack Examples Cucumber CLI, a sample [Cucumber CLI Runner](https://github.com/browserstackce/bstack-cucumber-cli-runner) test repository.

[![Java CI with Maven](https://github.com/BrowserStackCE/browserstack-examples-cucumber-cli/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/BrowserStackCE/browserstack-examples-cucumber-cli/actions/workflows/maven.yml)

## :pushpin: Prerequisites
Ensure you have the following dependencies installed on the machine

1. Java Development Kit (8 or above)
2. Maven (3 or above)
3. [BrowserStack Automate Account](https://www.browserstack.com/automate). [![BrowserStack](https://img.shields.io/badge/For-BrowserStackAutomate-orange)]()

## :pushpin: Usage

:arrow_down: Clone the Cucumber CLI Runner repository

```git
git clone git@github.com:BrowserStackCE/bstack-cucumber-cli-runner.git
```
<br/>

:package: Install the JAR in your local maven repository
```sh
mvn clean install
```
<br/>

:arrow_down: Clone this repository

```git
git clone git@github.com:BrowserStackCE/browserstack-examples-cucumber-cli.git
```
<br/>

:arrow_down: Run the repository using the following command

```shell
mvn test -P bstack-parallel-browsers
```

<br/>

