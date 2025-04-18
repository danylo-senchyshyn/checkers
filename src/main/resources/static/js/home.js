// home.js
document.addEventListener("DOMContentLoaded", () => {
    const openModalBtn = document.getElementById('openModal');
    const modal = document.getElementById('playerModal');
    const confirmBtn = document.getElementById('confirmSelection');
    const closeModalBtn = document.getElementById('closeModal');

    // Всегда скрываем модалку при загрузке
    modal.style.display = 'none';

    // Открытие модалки
    openModalBtn.addEventListener('click', () => {
        modal.style.display = 'flex';
    });

    // Подтверждение выбора игроков
    confirmBtn.addEventListener('click', () => {
        const p1 = document.getElementById('player1Name').value.trim();
        const p2 = document.getElementById('player2Name').value.trim();
        const avatar1 = document.getElementById('avatarDisplay1').src;
        const avatar2 = document.getElementById('avatarDisplay2').src;

        if (p1 && p2) {
            localStorage.setItem('player1', p1);
            localStorage.setItem('player2', p2);
            localStorage.setItem('avatar1', avatar1);
            localStorage.setItem('avatar2', avatar2);

            modal.style.display = 'none';
            window.location.href = `/checkers?player1Name=${encodeURIComponent(p1)}&player2Name=${encodeURIComponent(p2)}&avatar1=${encodeURIComponent(avatar1)}&avatar2=${encodeURIComponent(avatar2)}`;
        } else {
            alert("Please enter both player names.");
        }
    });

    // Закрытие модалки по кнопке
    closeModalBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    // Закрытие модалки по клику вне окна
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });
});