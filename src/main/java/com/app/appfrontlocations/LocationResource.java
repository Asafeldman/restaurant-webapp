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
        String locationId = updatedLocation.getId();
        String newName = updatedLocation.getName();
        String newAddress = updatedLocation.getAddress();
        String newStatus = updatedLocation.getStatus();
        if (locationDAO.updateLocation(locationId, newName, newAddress, newStatus)) {
            return Response.status(Response.Status.CREATED).entity(updatedLocation).build();
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
            return Response.ok().status(Response.Status.NO_CONTENT)
                    .entity("Location " + id + " deleted").build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }
}