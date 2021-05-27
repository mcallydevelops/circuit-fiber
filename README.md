**# circuit-fiber**

https://github.com/davenverse/circuit library helps to prevent cascading system failures by using circuit breakers
`` 
        
        val networkCall = IO.delay(...) 
        val circuitBreaker = CircuitBreaker.of[IO](
                maxFailures = 5,
                resetTimeout = 10.seconds,
                exponentialBackoffFactor = 2,
                maxResetTimeout = 10.minutes
        )

        circuitBreaker.flatMap(_.protect(networkCall)) 

**Notes**
- Doesn't Appear to work with cats effects 3 yet 
- No real documentation outside of the code 