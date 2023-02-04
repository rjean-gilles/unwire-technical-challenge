# Unwire Technical Challenge

### Model

For illustration purpose, I defined the Journey object as a data class
with a few fields that I felt would not be out of place in a real Journey model.

### Implementation

I implemented the REST application in Kotlin, using Spring Boot along with Spring WebFlux.
Thanks to WebFlux, the whole flow uses coroutines.
For ease of testing, I also implemented endpoints for creation/deletion, in addition to the 2 requested GET endpoints. 

The cache itself (```JourneyRepository```) is implemented with in-memory Java data structures (see ```JourneyRepositoryImpl```), and is thus non-persistent.
The cache uses coroutines and only exposes suspending functions, which is technically
not necessary here because everything is done in-memory (so there is no I/O pause to speak of). 
However, a real production cache would involve I/O pauses and delays, and would therefore ideally
be implemented with coroutines to avoid blocking.
So by only exposing suspending functions in ```JourneyRepository```, we ensure
that we can write a production-ready cache 
(by example backed by MongoDB or ElasticSearch) 
that would be a drop-in replacement, while still not inroducing any blocking.

### Performance

As required, there is a test case to evaluate the performance of the API, 
in the form of a unit test named ```PerfTests```.

As unit tests are not really suited to performance/load testing, I also 
created some [Gatling](https://gatling.io) simulations.
To run the simulations, first start the server locally by running the "bootRun" gradle task, then run the gradle "gatlingRun" task
```
> gradlew bootRun
.....

# In another terminal:
> gradlew gatlingRun
.....
```
Gatling will run the queries defined in the simulation files (see ```src\gatling\kotlin\journeyapi```) 
and print a performance report on the standard output.

