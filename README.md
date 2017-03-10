# TODO Service
Simple service to manage a TODO list

# Requirements
You should have Docker installed and running

# Starting the service

Start cassandra:  
`bash bin/run-cassandra.sh`

Start the service:  
`bash bin/run-todo-service.sh`

The API should then be available at: `127.0.0.1:8011/api/v1/todos`

# API
The service exposes the following API resources:

Get a list of TODOs:  
`GET /api/v1/todos`

Create a TODO:  
`POST /api/v1/todos`

Get a single TODO:  
`GET /api/v1/todos/{id}`

Remove a TODO:  
`DELETE /api/v1/todos/{id}`
