// REVIEWS.JS
function openModal() {
    document.getElementById('reviewModal').style.display = 'flex';
}

function closeModal() {
    document.getElementById('reviewModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('reviewModal');
    if (event.target == modal) {
        closeModal();
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const addButton = document.querySelector('.add-review-button');
    if (addButton) {
        addButton.addEventListener('click', function (event) {
            event.preventDefault();
            openModal();
        });
    }
});