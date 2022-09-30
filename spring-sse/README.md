# SSE (Server Sent Event)
SSE only can push data to client.<br>
Online stock quotes, browser notifications can be a good example of SSE.<br><br>
<img src="https://user-images.githubusercontent.com/17774927/193328676-94e4ec21-c3dd-42df-81f6-0db2d8bfbb4c.gif" alt="example" />

## Advantages SSE over WebSocket
- transported over simple HTTP
- built-in support for re-connection and event id
- no trouble with corporate firewalls doing packet inspection

## Usages
After executing below commands, you can access with http://localhost:3000 on any browsers except IE.

    ** build, run **
    # server
    cd spring-sse-server/ && chmod +x ./gradlew && ./gradlew build && java -jar build/libs/spring-sse-server-1.0.0-SNAPSHOT.jar &
    
    # client
    cd sse-client && yarn install && yarn run dev


## References
- https://stackoverflow.com/questions/5195452/websockets-vs-server-sent-events-eventsource
