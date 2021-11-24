# PDIOT_TeamD

24/11
## Added DataHandler Class
### Examples

#### First Init the class
```java
// Create dataHandler with buffer_size
dataHandler = new DataHandler(buffer_size);
```

#### Obtain respeck and thingy liveData
```kotlin
// Obtain Serialized Live data
val respeckLiveData = intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
val thingyLiveData = intent.getSerializableExtra(Constants.THINGY_LIVE_DATA) as ThingyLiveData
```

#### Feed into dataHandler
```java
dataHandler.feed(respeckLiveData)
dataHandler.feed(thingyLiveData)
```

#### Obtain data for training
dataHandler.
