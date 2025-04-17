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
let currentIndex2 = 0;

document.addEventListener("DOMContentLoaded", () => {
    // Загружаем сохранённые данные
    const savedPlayer1 = localStorage.getItem('player1');
    const savedPlayer2 = localStorage.getItem('player2');
    const savedAvatar1 = localStorage.getItem('avatar1');
    const savedAvatar2 = localStorage.getItem('avatar2');

    if (savedPlayer1) {
        document.getElementById('player1Name').value = savedPlayer1;
    }
    if (savedPlayer2) {
        document.getElementById('player2Name').value = savedPlayer2;
    }
    if (savedAvatar1) {
        document.getElementById('avatarDisplay1').src = savedAvatar1;
        currentIndex1 = avatars.indexOf(savedAvatar1);
    }
    if (savedAvatar2) {
        document.getElementById('avatarDisplay2').src = savedAvatar2;
        currentIndex2 = avatars.indexOf(savedAvatar2);
    }

    // Навигация по аватарам
    document.getElementById('prevAvatar1').addEventListener('click', () => {
        currentIndex1 = (currentIndex1 - 1 + avatars.length) % avatars.length;
        document.getElementById('avatarDisplay1').src = avatars[currentIndex1];
        localStorage.setItem('avatar1', avatars[currentIndex1]);
    });

    document.getElementById('nextAvatar1').addEventListener('click', () => {
        currentIndex1 = (currentIndex1 + 1) % avatars.length;
        document.getElementById('avatarDisplay1').src = avatars[currentIndex1];
        localStorage.setItem('avatar1', avatars[currentIndex1]);
    });

    document.getElementById('prevAvatar2').addEventListener('click', () => {
        currentIndex2 = (currentIndex2 - 1 + avatars.length) % avatars.length;
        document.getElementById('avatarDisplay2').src = avatars[currentIndex2];
        localStorage.setItem('avatar2', avatars[currentIndex2]);
    });

    document.getElementById('nextAvatar2').addEventListener('click', () => {
        currentIndex2 = (currentIndex2 + 1) % avatars.length;
        document.getElementById('avatarDisplay2').src = avatars[currentIndex2];
        localStorage.setItem('avatar2', avatars[currentIndex2]);
    });
});