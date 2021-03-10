# Jogging tracker API
#### ![#f03c15](https://via.placeholder.com/15/f03c15/000000?text=+) `#P.S.: This app was built for only demonstration purposes and not for use in production`


Application developed from below technical requirements:

Write a REST API that tracks jogging times of users
* API Users must be able to create an account and log in.
* All API calls must be authenticated.
* Implement at least three roles with different permission levels: a regular user would only be able to CRUD on their owned records, a user manager would be able to CRUD only users, and an admin would be able to CRUD all records and users.
* Each time entry when entered has a date, distance, time, and location.
* Based on the provided date and location, API should connect to a weather API provider and get the weather conditions for the run, and store that with each run.
* The API must create a report on average speed & distance per week.
* The API must be able to return data in the JSON format.
* The API should provide filter capabilities for all endpoints that return a list of elements, as well should be able to support pagination.
* The API filtering should allow using parenthesis for defining operations precedence and use any combination of the available fields. The supported operations should at least include or, and, eq (equals), ne (not equals), gt (greater than), lt (lower than).
    Example -> (date eq '2016-05-01') AND ((distance gt 20) OR (distance lt 10)).
* New users need to verify their account by email. Users should not be able to log in until this verification is complete.
* When a user fails to log in three times in a row, their account should be blocked automatically, and only admins and managers should be able to unblock it.
* An admin should be able to invite someone to the application by specifying an email address; the system should then send an invitation message automatically, prompting the user to complete the registration by setting first name, last name, and password.
* Users have to be able to upload and change their profile picture.
* Write unit and e2e tests.

## Prerequisites
Make sure you've installed tools listed below before setting up environment:

* Java (1.8)
* MySQL (version 8)

Also make sure that port 8080 is free for use.

You should change some credentials in [application.properties](src/main/resources/application.properties) such as DB, email, weather API username/password

## Installation and running
##### First clone the project:
* `git clone <REPO-URL>
* `cd jogging-tracker-api`
##### Install additional libs to local maven repository:
* `sh ./libs/install_local_repo.sh`
##### Compile, Test and Run
* `./mvnw compile install spring-boot:run`

## Usage

Application will populate initial data to the DB (to the 'tracker' schema) on startup

* Initial users:
---
    * email: admin@admin
    * password: 123
    * role: ADMIN
    
    *********************
    
    * email: mgr@mgr
    * password: 123
    * role: MANAGER
    
    *********************
    
    * email: usr@usr
    * password: 123
    * role: USER
    

