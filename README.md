# Requires JDK 25.

# how to start tests
export DB_URL=jdbc:postgresql://localhost:5432/todo_app_test \
export DB_USERNAME=postgres \
export DB_PASSWORD=1234 \
export JWT_SECRET="this-is-a-development-secret-key-that-is-at-least-32-bytes-long" \
mvn clean test

# how to start server and do manual testing

## 1. start server
if server is in use, run this command: `sudo lsof -i :8080` and kill the process, then try running the next commands: 

export JWT_SECRET="this-is-a-development-secret-key-that-is-at-least-32-bytes-long" \
export DB_PASSWORD="1234" 

mvn clean compile exec:java

## 2. set headers in bruno (disable all initially)
Accept: application/json \
Content-Type: application/json \
Authorization: Bearer 

also set body to json

## 3. register a user
POST http://127.0.0.1:8080/api/auth/register
```
{
  "username": "bruno",
  "password": "password123"
}
```

## 4. login as a user
POST http://127.0.0.1:8080/api/auth/login

```
{
  "username": "bruno",
  "password": "password123"
}
```

this returns a security token, save this token

## 5. create a todo list
paste the token in the header next to authorization : Bearer

enable all the headers

POST http://127.0.0.1:8080/api/lists

```
{
  "name": "Shopping"
}
```
remember the id

## 6. get lists
empty the body

GET http://127.0.0.1:8080/api/lists

## 7. create a todo item
POST http://127.0.0.1:8080/api/items

```
{
  "name": "Buy milk",
  "deadline": "2026-12-31T23:59:59Z"
}
```
remember the item's id

## 8. add todo item to a list
empty the body

if the if of the list is 1 and the id of the item is 1
then the link is

POST http://127.0.0.1:8080/api/lists/1/items/1

## 9. get items in list
GET http://127.0.0.1:8080/api/lists/1/items

(the 1 after lists/ is the id of which list you want to get the items from)

## 10. update an item
PUT http://127.0.0.1:8080/api/items/1

(the 1 after items/ is the id of the todo item)

```
{
  "name": "Buy milk and bread",
  "deadline": "2026-12-31T23:59:59Z"
}
```

## 11. toggle an item
empty the body

PATCH http://127.0.0.1:8080/api/items/1/toggle

(the 1 after items/ is the id of the item you want to toggle)

sending the same request/command changes the completed state from false to true and from true to false

## 12. delete an item form a list
empty the body 

DELETE http://127.0.0.1:8080/api/lists/1/items/1

## 13. delete an item
empty the body 

DELETE http://127.0.0.1:8080/api/items/1


---
complete endpoint list
```
AUTH
├── POST /api/auth/register
└── POST /api/auth/login

LISTS
├── GET    /api/lists
├── POST   /api/lists
├── GET    /api/lists/{id}
├── PUT    /api/lists/{id}
├── DELETE /api/lists/{id}
└── PATCH  /api/lists/{id}/complete

ITEMS
├── GET    /api/items
├── POST   /api/items
├── GET    /api/items/{id}
├── PUT    /api/items/{id}
├── DELETE /api/items/{id}
└── PATCH  /api/items/{id}/toggle

LIST ↔ ITEM
├── GET    /api/lists/{listId}/items
├── POST   /api/lists/{listId}/items/{itemId}
└── DELETE /api/lists/{listId}/items/{itemId}
```
