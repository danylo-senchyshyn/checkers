// player_vatar.js
const avatars = [
    "/images/player_avatar/bluered_glasses.gif",
    "/images/player_avatar/dead.gif",
    "/images/player_avatar/dreadlocks.gif",
    "/images/player_avatar/elprimo.gif",
    "/images/player_avatar/frog.gif",
    "/images/player_avatar/frog_orange_hat.gif",
    "/images/player_avatar/jesus.gif",
    "/images/player_avatar/mcdonalds.gif",
    "/images/player_avatar/ninja.gif",
    "/images/player_avatar/ninja_white_hat.gif"
];

let currentIndex1 = 0;
let currentIndex2 = 1;

document.addEventListener("DOMContentLoaded", () => {
    const openModalBtn = document.getElementById('openModal');
    const modal = document.getElementById('playerModal');
    const confirmBtn = document.getElementById('confirmSelection');
    const closeModalBtn = document.getElementById('closeModal');

    const avatarDisplay1 = document.getElementById('avatarDisplay1');
    const avatarDisplay2 = document.getElementById('avatarDisplay2');
    const prevBtn1 = document.getElementById('prevAvatar1');
    const nextBtn1 = document.getElementById('nextAvatar1');
    const prevBtn2 = document.getElementById('prevAvatar2');
    const nextBtn2 = document.getElementById('nextAvatar2');

    // Начальные аватарки
    avatarDisplay1.src = avatars[currentIndex1];
    avatarDisplay2.src = avatars[currentIndex2];

    modal.style.display = 'none';

    openModalBtn.addEventListener('click', () => {
        modal.style.display = 'flex';
    });

    confirmBtn.addEventListener('click', () => {
        const p1 = document.getElementById('player1Name').value.trim();
        const p2 = document.getElementById('player2Name').value.trim();
        const avatar1 = avatarDisplay1.src;
        const avatar2 = avatarDisplay2.src;

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

    closeModalBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });

    prevBtn1.addEventListener('click', () => {
        currentIndex1 = (currentIndex1 - 1 + avatars.length) % avatars.length;
        avatarDisplay1.src = avatars[currentIndex1];
    });
    nextBtn1.addEventListener('click', () => {
        currentIndex1 = (currentIndex1 + 1) % avatars.length;
        avatarDisplay1.src = avatars[currentIndex1];
    });

    prevBtn2.addEventListener('click', () => {
        currentIndex2 = (currentIndex2 - 1 + avatars.length) % avatars.length;
        avatarDisplay2.src = avatars[currentIndex2];
    });
    nextBtn2.addEventListener('click', () => {
        currentIndex2 = (currentIndex2 + 1) % avatars.length;
        avatarDisplay2.src = avatars[currentIndex2];
    });
});