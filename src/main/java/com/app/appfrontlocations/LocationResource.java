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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocationById(@PathParam("id") String id) {
        Location location = locationDAO.getLocationById(id);

        if (location != null) {
            return Response.status(Response.Status.OK).entity(location).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("No locations found for id: " + id).build();
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

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") String id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Location id is missing").build();
        }
        if (locationDAO.deleteLocation(id)) {
            return Response.ok().status(Response.Status.OK)
                    .entity("Location ID " + id + " deleted").build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
}