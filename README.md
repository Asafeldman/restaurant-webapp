# AppFront - Locations API

This is a Java-based RESTful API for managing location data.

## API Endpoints

- `GET /api/locations` - Retrieve a list of all locations.
- `GET /api/locations/{id}` - Retrieve details of a specific location by ID.
- `POST /api/locations/add` - Add a new location.
- `POST /api/locations/update` - Update an existing location.
- `DELETE /api/locations/delete/{id}` - Delete a location by ID.

## Build and Run

1. Clone the repository.
2. Configure your MongoDB connection in `MongoDBUtil.java`.
3. Install project dependencies using Maven:

- ...bash
- cd appfront
- mvn install

4. Build and run the project:

- mvn clean package
- java -jar target/appfront-1.0-SNAPSHOT.jar

5. The application will start at "http://localhost:8080/appfrontLocations_war_exploded/api/locations."
   

