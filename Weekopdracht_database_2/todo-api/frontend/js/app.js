const API_URL = "http://127.0.0.1:8080";

// Application state
let selectedListId = null;
let lists = [];

// HTML elements
const authSection = document.getElementById("auth-section");
const appSection = document.getElementById("app-section");
const logoutButton = document.getElementById("logout-button");
const loginForm = document.getElementById("login-form");
const registerForm = document.getElementById("register-form");
const createListForm = document.getElementById("create-list-form");
const createItemForm = document.getElementById("create-item-form");
const listsContainer = document.getElementById("lists-container");
const itemsContainer = document.getElementById("items-container");
const selectedListTitle = document.getElementById("selected-list-title");
const emptyState = document.getElementById("empty-state");
const message = document.getElementById("message");

// Make an HTTP request to the Java API.
async function apiRequest(path, options = {}) {
    const token = localStorage.getItem("token");

    const headers = {
        Accept: "application/json",
        ...options.headers
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    if (options.body !== undefined) {
        headers["Content-Type"] = "application/json";
    }

    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers
    });

    const responseText = await response.text();

    let data = null;

    if (responseText) {
        try {
            data = JSON.parse(responseText);
        } catch {
            data = responseText;
        }
    }

    if (!response.ok) {
        if (response.status === 401) {
            localStorage.removeItem("token");
            showLogin();
        }

        const errorMessage =
            typeof data === "object" && data?.error
                ? data.error
                : `Request failed (${response.status})`;

        throw new Error(errorMessage);
    }

    return data;
}

// Display a message to the user.
function showMessage(text, isError = false) {
    message.textContent = text;
    message.style.color = isError ? "#c0392b" : "#237547";
}

function showLogin() {
    authSection.hidden = false;
    appSection.hidden = true;
    logoutButton.hidden = true;
    selectedListId = null;
}

function showDashboard() {
    authSection.hidden = true;
    appSection.hidden = false;
    logoutButton.hidden = false;
}

// LOGIN
loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(loginForm);

    try {
        const result = await apiRequest("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({
                username: formData.get("username"),
                password: formData.get("password")
            })
        });

        localStorage.setItem("token", result.token);

        loginForm.reset();
        showDashboard();
        showMessage("Login successful.");

        await loadLists();
    } catch (error) {
        showMessage(error.message, true);
    }
});

// REGISTER
registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(registerForm);

    try {
        await apiRequest("/api/auth/register", {
            method: "POST",
            body: JSON.stringify({
                username: formData.get("username"),
                password: formData.get("password")
            })
        });

        registerForm.reset();
        showMessage("Account created! You can now log in.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

// LOAD ALL LISTS
async function loadLists() {
    try {
        lists = await apiRequest("/api/lists");

        renderLists();

        if (
            selectedListId !== null &&
            lists.some(list => list.id === selectedListId)
        ) {
            await selectList(selectedListId);
        } else if (lists.length > 0) {
            await selectList(lists[0].id);
        } else {
            selectedListId = null;
            selectedListTitle.textContent = "No lists yet";
            createItemForm.hidden = true;
            itemsContainer.replaceChildren();
            emptyState.hidden = false;
            emptyState.textContent =
                "Create your first list to get started.";
        }
    } catch (error) {
        showMessage(error.message, true);
    }
}

// // RENDER LISTS
// function renderLists() {
//     listsContainer.replaceChildren();

//     lists.forEach((list) => {
//         const listItem = document.createElement("li");
//         const button = document.createElement("button");

//         button.type = "button";
//         button.textContent =
//             `${list.name}${list.completed ? " ✓" : ""}`;

//         button.classList.toggle(
//             "selected",
//             list.id === selectedListId
//         );

//         button.addEventListener("click", () => {
//             selectList(list.id);
//         });

//         listItem.appendChild(button);
//         listsContainer.appendChild(listItem);
//     });
// }

// RENDER LISTS
function renderLists() {
listsContainer.replaceChildren();

lists.forEach((list) => {
    const listItem = document.createElement("li");
    const button = document.createElement("button");
    const deleteButton = document.createElement("button");

    // List selection button
    button.type = "button";
    button.textContent =
        list.name + (list.completed ? " ✓" : "");

    button.classList.toggle(
        "selected",
        list.id === selectedListId
    );

    button.addEventListener("click", () => {
        selectList(list.id);
    });

    // Delete list button
    deleteButton.type = "button";
    deleteButton.textContent = "Delete";
    deleteButton.className = "delete-button";

    deleteButton.addEventListener("click", async () => {
        const confirmed = confirm(
            "Are you sure you want to delete the list \"" +
            list.name + "\"?"
        );

        if (!confirmed) {
            return;
        }

        deleteButton.disabled = true;

        try {
            await apiRequest("/api/lists/" + list.id, {
                method: "DELETE"
            });

            if (selectedListId === list.id) {
                selectedListId = null;
            }

            await loadLists();
            showMessage("List deleted.");
        } catch (error) {
            deleteButton.disabled = false;
            showMessage(error.message, true);
        }
    });

    // Add both buttons to the list item
    listItem.classList.add("list-row");
    listItem.append(button, deleteButton);
    listsContainer.appendChild(listItem);
});

}

// SELECT A LIST AND LOAD ITS ITEMS
async function selectList(listId) {
    selectedListId = listId;

    const selectedList = lists.find(
        list => list.id === listId
    );

    if (!selectedList) {
        return;
    }

    selectedListTitle.textContent = selectedList.name;
    createItemForm.hidden = false;

    renderLists();

    try {
        const items = await apiRequest(
            `/api/lists/${listId}/items`
        );

        renderItems(items);
    } catch (error) {
        showMessage(error.message, true);
    }
}

// RENDER TASKS
function renderItems(items) {
    itemsContainer.replaceChildren();
    emptyState.hidden = items.length > 0;

    if (items.length === 0) {
        emptyState.textContent =
            "No tasks in this list yet. Add one above.";
        return;
    }

    items.forEach((item) => {
        const listItem = document.createElement("li");
        const checkbox = document.createElement("input");
        const details = document.createElement("div");
        const name = document.createElement("span");
        const deadline = document.createElement("small");

        checkbox.type = "checkbox";
        checkbox.checked = item.completed;
        checkbox.setAttribute(
            "aria-label",
            `Complete ${item.name}`
        );

        checkbox.addEventListener("change", async () => {
            checkbox.disabled = true;

            try {
                await apiRequest(
                    `/api/items/${item.id}/toggle`,
                    { method: "PATCH" }
                );

                showMessage("Task updated.");
                await loadLists();
            } catch (error) {
                checkbox.checked = item.completed;
                checkbox.disabled = false;
                showMessage(error.message, true);
            }
        });

        name.textContent = item.name;
        name.style.display = "block";

        if (item.completed) {
            name.style.textDecoration = "line-through";
            name.style.color = "#687386";
        }

        // multiply date to fix numeric timestamps for iso strings, 1000x to go from milliseconds to seconds
        const deadlineDate = new Date(
            typeof item.deadline === "number"
            ? item.deadline * 1000
            : item.deadline
        );

        const isLate =
            !Number.isNaN(deadlineDate.getTime()) &&
            deadlineDate.getTime() < Date.now();

        deadline.textContent = Number.isNaN(deadlineDate.getTime())
            ? `Deadline: ${item.deadline}`
            : `Deadline: ${deadlineDate.toLocaleString()}`;

        deadline.style.display = "block";
        deadline.style.color = isLate ? "#c0392b" : "#687386";

        if (isLate) {
            deadline.textContent += " — Overdue";
        }

        // details.append(name, deadline);
        // listItem.append(checkbox, details);
        // itemsContainer.appendChild(listItem);
        const deleteButton = document.createElement("button");
              deleteButton.type = "button";
              deleteButton.textContent = "Delete";
              deleteButton.className = "delete-button";

              deleteButton.addEventListener("click", async () => {
        const confirmed = confirm(
        "Are you sure you want to delete \"" + item.name + "\"?"
        );

        if (!confirmed) {
            return;
        }

        deleteButton.disabled = true;

        try {
            await apiRequest(`/api/items/${item.id}`, {
                method: "DELETE"
            });

            showMessage("Task deleted.");
            await loadLists();
        } catch (error) {
            deleteButton.disabled = false;
            showMessage(error.message, true);
        }

        });

        details.append(name, deadline);
        listItem.append(checkbox, details, deleteButton);
        itemsContainer.appendChild(listItem);
    });
}

// CREATE A LIST
createListForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(createListForm);
    const name = formData.get("name").trim();

    if (!name) {
        showMessage("Please enter a list name.", true);
        return;
    }

    try {
        const createdList = await apiRequest("/api/lists", {
            method: "POST",
            body: JSON.stringify({ name })
        });

        createListForm.reset();

        await loadLists();

        if (createdList?.id !== undefined) {
            await selectList(createdList.id);
        }

        showMessage("List created successfully.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

// CREATE A TASK AND ADD IT TO THE SELECTED LIST
createItemForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    if (selectedListId === null) {
        showMessage("Please select a list first.", true);
        return;
    }

    const formData = new FormData(createItemForm);
    const name = formData.get("name").trim();
    const deadlineInput = formData.get("deadline");

    if (!name || !deadlineInput) {
        showMessage("Enter a task name and deadline.", true);
        return;
    }

    // Interpret the chosen date as the end of that day
    // in the user's local timezone, then send an ISO timestamp.
    const deadlineDate = new Date(`${deadlineInput}T23:59:59`);
    
    // console.log("Selected deadline:", deadlineInput); // debug line
    // console.log("Deadline sent to API:", deadlineDate.toISOString()); // debug line

    try {
        const createdItem = await apiRequest("/api/items", {
            method: "POST",
            body: JSON.stringify({
                name,
                deadline: deadlineDate.toISOString()
            })
        });

        // console.log("Task returned by API:", createdItem); // debug line

        if (createdItem?.id === undefined) {
            throw new Error(
                "The task was created, but its ID was not returned. " +
                "Check the API response before linking it to a list."
            );
        }

        await apiRequest(
            `/api/lists/${selectedListId}/items/${createdItem.id}`,
            { method: "POST" }
        );

        createItemForm.reset();

        await loadLists();

        showMessage("Task created and added to the list.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

// LOG OUT
logoutButton.addEventListener("click", () => {
    localStorage.removeItem("token");
    lists = [];
    selectedListId = null;

    listsContainer.replaceChildren();
    itemsContainer.replaceChildren();
    createListForm.reset();
    createItemForm.reset();

    showLogin();
    showMessage("You have logged out.");
});

// RESTORE A SESSION WHEN POSSIBLE
async function initializeApp() {
    if (!localStorage.getItem("token")) {
        showLogin();
        return;
    }

    showDashboard();
    await loadLists();
}

initializeApp();
