# Unwire Technical Challenge

### Model

For illustration purpose, I defined the Journey object as a data class
with a few fields that I felt would not be out of place in a real Journey model.
Of course any model would work just fine and not change anything to the rest of 
the code (no assumptions were made except for the existence of an ```id``` field
in the `` Journey`` class).

### Implementation

I implemented the REST application in Kotlin, using Spring Boot along with Spring WebFlux.
Thanks to WebFlux, the whole flow uses coroutines.
For ease of testing, I also implemented endpoints for creation/deletion, in addition to the 2 requested GET endpoints. 

The cache itself (```JourneyRepository```) is implemented with in-memory Java data structures (see ```JourneyRepositoryImpl```), and is thus non-persistent.
The cache uses coroutines and only exposes suspending functions, which is technically
not necessary here because everything is done in-memory (so there is no I/O pause to speak of). 
However, a real production cache would involve I/O pauses and delays, and would therefore ideally
be implemented with coroutines to avoid blocking.
So by only exposing suspending functions in ```JourneyRepository```, I ensure
that I can write a production-ready cache 
(by example backed by MongoDB or ElasticSearch) 
that would be a drop-in replacement, while still not introducing any blocking.

Note that I also implemented a simple User repository just so I can check in the API that the specified user id
references an existing user.

### Validations

I explicitly handle the following error cases:

- Invalid user id. In other words, the specified user id 
  does not correspond to any existing user. This means that 
  I had to implement a user repository to implement this check.
- Invalid journey id. In other words the specified journey id
  does not correspond to any existing journey.
- An id was specified in a POST request (to create a new user/journey).
  This is needed because I use the same model 
  (```User``` and ```Journey``` data classes) for both the POST and 
  GET requests. I simply defined the ```id``` field as nullable, so 
  that it can be omitted in the POST request (the id will be assigned by the API).
  To avoid any ambiguity on the fact that the id is assigned by the API 
  (as opposed to being set by the caller), it is best to forbid 
  specifying the id altogether in a POST request.

### About the user id

The instructions say that the endpoints need to receive the user id as a request header ```api-user-id```.
This however seems to be contradicted by the fact that the second endpoint already takes the user id as part 
of the path (```GET /user/:user_id/journeys```).
I had to choose between one or the other. I ended up choosing to keep the user 
id as part of the path, and not as a header, in good part because it fits better
with the REST principles anyway. Indeed if the user id is specified via a header 
and not via the path, we will end up having many distinct ```Journey``` resources 
being served under the same URL, whereas the URL is supposed to uniquely identify
a resource.

### Running and testing the API

```
$ gradlew bootRun
```
The server will listen on port 8080.

### Performance

As required, there is a test case to evaluate the performance of the cache 
(the Journey repository), in the form of a JUnit test case (```JourneyRepositoryPerfTests```)
There is also a corresponding test case at the Web Layer (performing actual HTTP requests)
in the form of a unit test named ```JourneyAPIPerfTests```.
The time taken by the actual queries is logged to the output, because 
relying on the test case duration reported by JUnit is not reliable (as it also includes the time taken to load 
test data into the repository). 
By example:

`2023-02-05T14:27:39.815+01:00  INFO 42228 --- [    Test worker] AbstractJourneyPerfTests                 : PERF TEST JourneyRepositoryPerfTests.getAllJourneysByUserIdPerfTest lasted 485ms for 20000 requests (~41237 requests per sec)`

As can easily be seen from comparing the results of ```JourneyRepositoryPerfTests``` 
and ```JourneyAPIPerfTests```, most of the time is spent in the routing 
and marshalling of the requests and response. The time spent in the actual Journey repository is negligible.

As unit tests are not really suited to performance/load testing, I also 
created some [Gatling](https://gatling.io) simulations.
To run the simulations, first start the server locally by running the "bootRun" gradle task. You can set the 
```load-test-data``` system property to true in order to preload test data into the cache, for more realistic
testing conditions (otherwise the cache will be empty, as it is non-persistent).
```
$ gradlew bootRun -Dload-test-data=true
.....
```
Then run the gradle "gatlingRun" task (in another terminal):
```
$ gradlew gatlingRun
.....
```
Gatling will run the queries defined in the simulation files (see ```src\gatling\kotlin\journeyapi```). 
At the end it will print a performance summary on the standard output and 
generate a report that can be opened in a web browser.

