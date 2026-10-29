package com.example.todo.resource;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.example.todo.dao.ListItemDAO;
import com.example.todo.dao.ListItemDAOImpl;
import com.example.todo.dao.TodoListDAO;
import com.example.todo.dao.TodoListDAOImpl;
import com.example.todo.model.TodoItem;
import com.example.todo.model.TodoList;
import com.example.todo.service.TodoListService;

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

@Path("/api/lists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoListResource {

    private final TodoListService todoListService;

    public TodoListResource() {
        TodoListDAO todoListDAO =
                new TodoListDAOImpl();

        ListItemDAO listItemDAO =
                new ListItemDAOImpl();

        this.todoListService =
                new TodoListService(
                        todoListDAO,
                        listItemDAO
                );
    }

    @GET
    public Response getAll(
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        return Response.ok(
                todoListService.getAll(userId)
        ).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(
            @PathParam("id") Long id,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        return todoListService
                .getById(id, userId)
                .map(list ->
                        Response.ok(list).build()
                )
                .orElseGet(() ->
                        Response.status(
                                Response.Status.NOT_FOUND
                        ).build()
                );
    }

    @POST
    public Response create(
            TodoList list,
            @Context SecurityContext securityContext
    ) {
        try {
            Long userId = getUserId(securityContext);

            TodoList created =
                    todoListService.create(
                            list,
                            userId
                    );

            return Response.created(
                    URI.create(
                            "/api/lists/" + created.getId()
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
            TodoList list,
            @Context SecurityContext securityContext
    ) {
        try {
            Long userId = getUserId(securityContext);

            return todoListService
                    .update(id, list, userId)
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

        if (!todoListService.delete(id, userId)) {
            return Response.status(
                    Response.Status.NOT_FOUND
            ).build();
        }

        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}/complete")
    public Response complete(
            @PathParam("id") Long id,
            TodoList list,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        boolean completed = list != null
                && list.isCompleted();

        return todoListService
                .setCompleted(
                        id,
                        userId,
                        completed
                )
                .map(updated ->
                        Response.ok(updated).build()
                )
                .orElseGet(() ->
                        Response.status(
                                Response.Status.NOT_FOUND
                        ).build()
                );
    }

    @GET
    @Path("/{listId}/items")
    public Response getItems(
            @PathParam("listId") Long listId,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        if (todoListService
                .getById(listId, userId)
                .isEmpty()) {

            return Response.status(
                    Response.Status.NOT_FOUND
            ).build();
        }

        List<TodoItem> items =
                todoListService.getItems(
                        listId,
                        userId
                );

        return Response.ok(items).build();
    }

    @POST
    @Path("/{listId}/items/{itemId}")
    public Response addItem(
            @PathParam("listId") Long listId,
            @PathParam("itemId") Long itemId,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        if (!todoListService.addItem(
                listId,
                itemId,
                userId
        )) {
            return Response.status(
                    Response.Status.NOT_FOUND
            ).build();
        }

        return Response.status(
                Response.Status.CREATED
        ).build();
    }

    @DELETE
    @Path("/{listId}/items/{itemId}")
    public Response removeItem(
            @PathParam("listId") Long listId,
            @PathParam("itemId") Long itemId,
            @Context SecurityContext securityContext
    ) {
        Long userId = getUserId(securityContext);

        if (!todoListService.removeItem(
                listId,
                itemId,
                userId
        )) {
            return Response.status(
                    Response.Status.NOT_FOUND
            ).build();
        }

        return Response.noContent().build();
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