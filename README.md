# TODO Service
Simple service to manage a TODO list

## Requirements
- docker
- sbt (in case you want to build)

## Build the service

To build the docker image of the service, run the following command:  
`sbt docker:publishLocal`

## Start the service

Start cassandra:  
`bash bin/run-cassandra.sh`

Start the service:  
`bash bin/run-todo-service.sh`

The API should then be available at:  
`127.0.0.1:8011/api/v1/todos`

## API
The service exposes the following API resources:

Get a list of TODOs:  
`GET /api/v1/todos`

Create a TODO:  
`POST /api/v1/todos`

Get a single TODO:  
`GET /api/v1/todos/{id}`

Remove a TODO:  
`DELETE /api/v1/todos/{id}`
