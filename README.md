1) The contacts API provides following functionality:

        - Add contact
        - Edit contact
        - Remove contact
        - Retrieve contact by id
        - Retrieve contact by email
        - Retrieve contact by phone

    Following business rules are enforced (HTTP 400 (bad request) is returned)
    
        - When we create a new contact or update existing one either email or phone must be presented
        - email address is unique and can't be duplicated
        - phone number is unique and can't be duplicated

2) The API is implemented by stateless microservice with SQL storage.

    Statelessness will facilitate easy horizontal scalability in cloud environment.
   
    The microservice is written in Java and containerized in docker. 
    Kubernetes configurations are provided for k8s deployment.

    Kubernetes ReplicaSet will implement scaling of the microservice instances.
    Kubernetes ClusterIP service will provide server-side load balancing.
 
  
3) In order to access the service endpoint a client should authenticate with an OpenID Connect authentication server, obtain a JWT access token and provide the access token in “Authorization” HTTP header in the form: “Bearer <token value>”

    The token should be signed with a private key in accordance with RSA-SHA256 schema.

    The microservice will validate token and signature against a public key exposed by OpenID Connect authentication server that issued the token. 

    The public key will be exposed by the authentication server in accordance with JWKS (JSON Web Key Set) spec.

    The microservice should be configured with a property that is URL to JWKS endpoint. (security.oauth2.resource.jwk.key-set-uri)
    The user that authenticated with the authentication server should have ‘contact-admin’ role.

    Any stateless instance of the microservice can validate JWT token. No sessions needed.

    
4) Versioning and upgrades: 

    Initial version of microservice will be exposed on ‘/v1/contacts’ URL

    If changes in new version of the microservice are breaking current API contract (consumers have to be changed) then we will expose the new version of microservice on ‘/v2/contacts’ URL. 

    We will have to maintain two versions of contract in the microservice

        ‘/v1/contacts’ – for an old clients
        ‘/v2/contacts’ – for a new clients.

    Old ‘/v1/contacts’ contract will be eliminated after all clients upgraded.


5) If the microservice is supposed to serve a web client it should be fronted with an API gateway that would off-load SSL traffic and route to the service.


6) Logging and Monitoring

    The microservice log management is completely outside the application code. The execution environment will handle that. The microservices should write log events into stdout or stderr. The app code should express what should be logged. Docker container writes content of stdout or stderr messages into log files directory on running node/vm. ELK stack or Splunk will handle log aggregation.
