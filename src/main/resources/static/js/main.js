document.addEventListener("DOMContentLoaded", () => {
    const addButton = document.getElementById("add-line");
    const list = document.getElementById("line-items");
    const template = document.getElementById("detail-row-template");
    if (!addButton || !list || !template) return;

    addButton.addEventListener("click", () => {
        const index = list.querySelectorAll(".line-item").length;
        const row = template.innerHTML.replaceAll("__index__", String(index));
        list.insertAdjacentHTML("beforeend", row);
    });
    list.addEventListener("click", (event) => {
        if (event.target instanceof Element && event.target.closest(".remove-line")) {
            event.target.closest(".line-item").remove();
        }
    });
});
