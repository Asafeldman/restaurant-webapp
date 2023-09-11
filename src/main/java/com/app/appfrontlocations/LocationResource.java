package com.app.appfrontlocations;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/locations")
public class LocationResource {
    private final LocationDAO locationDAO = new LocationDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocations() {
        List<Location> locationList = locationDAO.getAllLocations();

        if (!locationList.isEmpty()) {
            return Response.status(Response.Status.OK).entity(locationList).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No locations found").build();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocationByName(@PathParam("name") String name) {
        Location location = locationDAO.getLocationByField("_name", name);
        if (location != null) {
            return Response.status(Response.Status.OK).entity(location).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No locations named: " + name + " found").build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newLocation(Location newLocation) {
        if (newLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Location data is missing").build();
        }
        if (locationDAO.insertLocation(newLocation)) {
            return Response.status(Response.Status.CREATED).entity(newLocation).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateLocation(Location updatedLocation) {
        if (updatedLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Location data is missing").build();
        }
        if (locationDAO.updateLocation(updatedLocation)) {
            return Response.status(Response.Status.OK).entity(updatedLocation).build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }

    @POST
    @Path("/addorupdate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addOrUpdateLocation(Location updatedLocation) {
        if (updatedLocation == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Location data is missing").build();
        }
        if (locationDAO.addOrUpdateLocation(updatedLocation)) {
            return Response.status(Response.Status.OK).entity(updatedLocation).build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }

    @DELETE
    @Path("/delete/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("name") String name) {
        if (name == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Location id is missing").build();
        }
        if (locationDAO.deleteLocation(name)) {
            return Response.ok().status(Response.Status.OK)
                    .entity("Location " + name + " deleted").build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
}