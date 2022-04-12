# Getting Started

### Start Application
Start Main class

### Creating Orders by using REST Api
#### CMD curl request
curl -X POST localhost:8080/create-order -H "Content-type:application/json" -d "{\"type\": \"<BUY|SELL>\", \"price\": \"100\", \"symbol\": \"<AAA|BBB|CCC>\", \"quantity\": \"10\"}"
#### POSTMAN
![](C:\Users\PWhite\IdeaProjects\stock-market-simulator\postman-example.JPG)

### Receiving WebSocket messages
#### HTML client
open resources\index.html file in browser and connect to the default host (many thanks to https://github.com/thegeekyasian/WebSocket-SpringBoot)