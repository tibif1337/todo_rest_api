package com.example.todo.resource;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.example.todo.dao.TodoItemDAO;
import com.example.todo.dao.TodoItemDAOImpl;
import com.example.todo.model.TodoItem;
import com.example.todo.service.TodoItemService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoItemResource {

    private final TodoItemService todoItemService;

    public TodoItemResource() {
        TodoItemDAO todoItemDAO =
                new TodoItemDAOImpl();

        this.todoItemService =
                new TodoItemService(todoItemDAO);
    }

    @GET
    public Response getAll(
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        List<TodoItem> items =
                todoItemService.getAll(userId);

        return Response.ok(items).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(
            @PathParam("id") Long id,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        return todoItemService
                .getById(id, userId)
                .map(item ->
                        Response.ok(item).build()
                )
                .orElseGet(() ->
                        Response.status(
                                Response.Status.NOT_FOUND
                        ).build()
                );
    }

    @POST
    public Response create(
            TodoItem item,
            @Context SecurityContext securityContext
    ) {
        try {
            Long userId = getUserId(securityContext);

            TodoItem created =
                    todoItemService.create(
                            item,
                            userId
                    );

            return Response.created(
                    URI.create(
                            "/api/items/" + created.getId()
                    )
            ).entity(created).build();

        } catch (IllegalArgumentException exception) {
            return badRequest(exception);
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @PathParam("id") Long id,
            TodoItem item,
            @Context SecurityContext securityContext
    ) {
        try {
            Long userId = getUserId(securityContext);

            return todoItemService
                    .update(id, item, userId)
                    .map(updated ->
                            Response.ok(updated).build()
                    )
                    .orElseGet(() ->
                            Response.status(
                                    Response.Status.NOT_FOUND
                            ).build()
                    );

        } catch (IllegalArgumentException exception) {
            return badRequest(exception);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(
            @PathParam("id") Long id,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        if (!todoItemService.delete(id, userId)) {
            return Response.status(
                    Response.Status.NOT_FOUND
            ).build();
        }

        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}/toggle")
    public Response toggle(
            @PathParam("id") Long id,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        return todoItemService
                .toggle(id, userId)
                .map(item ->
                        Response.ok(item).build()
                )
                .orElseGet(() ->
                        Response.status(
                                Response.Status.NOT_FOUND
                        ).build()
                );
    }

    private Long getUserId(
            SecurityContext securityContext
    ) {
        return Long.valueOf(
                securityContext
                        .getUserPrincipal()
                        .getName()
        );
    }

    private Response badRequest(
            IllegalArgumentException exception
    ) {
        return Response.status(
                Response.Status.BAD_REQUEST
        ).entity(
                Map.of(
                        "error",
                        exception.getMessage()
                )
        ).build();
    }
}