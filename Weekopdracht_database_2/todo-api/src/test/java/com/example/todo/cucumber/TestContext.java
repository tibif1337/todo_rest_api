package com.example.todo.cucumber;

import com.fasterxml.jackson.databind.JsonNode;

public class TestContext {

    private String token;

    private Long listId;
    private Long secondListId;

    private Long itemId;
    private Long secondItemId;

    private String listName;
    private String secondListName;

    private String itemName;
    private String secondItemName;

    private int lastStatusCode;
    private JsonNode lastResponse;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public Long getSecondListId() {
        return secondListId;
    }

    public void setSecondListId(Long secondListId) {
        this.secondListId = secondListId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getSecondItemId() {
        return secondItemId;
    }

    public void setSecondItemId(Long secondItemId) {
        this.secondItemId = secondItemId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getSecondListName() {
        return secondListName;
    }

    public void setSecondListName(String secondListName) {
        this.secondListName = secondListName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSecondItemName() {
        return secondItemName;
    }

    public void setSecondItemName(String secondItemName) {
        this.secondItemName = secondItemName;
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public void setLastStatusCode(int lastStatusCode) {
        this.lastStatusCode = lastStatusCode;
    }

    public JsonNode getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(JsonNode lastResponse) {
        this.lastResponse = lastResponse;
    }
}