function openModal() {
    document.getElementById('reviewModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('reviewModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('reviewModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}