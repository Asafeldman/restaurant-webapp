# AppFront - Locations API

A Java-based RESTful API for managing restaurant location data.

## API Endpoints

- `GET /api/locations` - Retrieve a list of all locations.
- `GET /api/locations/{name}` - Retrieve details of a specific location by name.
- `POST /api/locations/add` - Add a new location.
- `POST /api/locations/update` - Update an existing location.
- `POST /api/locations/addorupdate` - Add a new location or update an existing location.
- `DELETE /api/locations/delete/{name}` - Delete a location by ID.

## Build and Run

1. Clone the repository.
2. Configure your MongoDB connection in `MongoDBUtil.java`.
3. Install project dependencies using Maven:

- ... bash
- cd appfront
- mvn install

4. Build and run the project:

- mvn clean package
- java -jar target/appfront-1.0-SNAPSHOT.jar

5. The application will start at "http://localhost:8080/appfrontLocations_war_exploded/api/locations."

## Project Structure

```
appfront/
└── src/
    └── main/
        ├── java/
        │   └── com.app.appfrontlocations/
        │       ├── Location
        │       ├── LocationApp
        │       ├── LocationDAO
        │       ├── LocationResource
        │       └── MongoDBUtil
        ├── exceptions/
        │   └── java/
        │       └── com.app.appfrontlocations/
        │           ├── LocationConversionException
        │           ├── LocationNotFoundException
        │           └── LocationOperationException
        └── test/
            └── java/
                └── com.app.appfrontlocations/
                    └── ConcurrencyTest
```