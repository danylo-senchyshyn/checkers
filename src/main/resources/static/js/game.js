// game.js
let firstCoordinates = null;
let possibleMoves = [];
let isWhiteTurn = true;

async function selectTile(row, col) {
    console.log(`Tile clicked: Row ${row}, Column ${col}`);
    const selectedTile = document.getElementById(`tile-${row}-${col}`);
    if (!selectedTile) {
        console.error(`Tile with ID tile-${row}-${col} not found.`);
        return;
    }

    const piece = selectedTile.querySelector('.piece-checker, .piece-king');

    if (firstCoordinates && firstCoordinates.row === row && firstCoordinates.col === col) {
        clearSelection();
        return;
    }

    // Проверяем возможные ходы
    if (firstCoordinates) {
        const isMoveValid = possibleMoves.some(move => move.row === row && move.col === col);
        if (isMoveValid) {
            executeMove(row, col);
            return;
        } else {
            clearSelection();
        }
    }

    if (!piece) {
        console.warn(`No piece found on tile-${row}-${col}.`);
        return;
    }

    const moves = await getPossibleMoves(row, col);
    if (moves.length === 0) {
        console.warn(`No possible moves for tile-${row}-${col}.`);
        return;
    }

    firstCoordinates = { row, col };
    possibleMoves = moves;
    selectedTile.classList.add('selected');
    highlightPossibleMoves(moves);
}

async function executeMove(row, col) {
    const url = `/checkers?fr=${firstCoordinates.row}&fc=${firstCoordinates.col}&tr=${row}&tc=${col}`;
    try {
        const response = await fetch(url, { method: 'GET' });
        if (response.ok) {
            window.location.href = "/checkers";
        } else {
            console.error("Ошибка при выполнении хода");
        }
    } catch (error) {
        console.error("Ошибка при запросе на выполнение хода:", error);
    }
}

async function getPossibleMoves(row, col) {
    try {
        const response = await fetch(`/checkers/moves?row=${row}&col=${col}`);
        if (!response.ok) throw new Error(`Failed to fetch moves for tile-${row}-${col}.`);
        const moves = await response.json();
        return moves.map(move => ({ row: move[0], col: move[1] }));
    } catch (error) {
        console.error('Ошибка при запросе ходов:', error);
        return [];
    }
}

function highlightPossibleMoves(moves) {
    moves.forEach(move => {
        const tile = document.getElementById(`tile-${move.row}-${move.col}`);
        if (tile) {
            tile.classList.add('possible-move');
        } else {
            console.warn(`Tile for possible move not found: tile-${move.row}-${move.col}`);
        }
    });
}

function clearSelection() {
    document.querySelectorAll('.tile').forEach(tile => {
        tile.classList.remove('selected');
        tile.classList.remove('possible-move');
    });

    firstCoordinates = null;
    possibleMoves = [];
}

function updatePlayerInfo() {
    const player1 = localStorage.getItem('player1');
    const player2 = localStorage.getItem('player2');
    const avatar1 = localStorage.getItem('avatar1');
    const avatar2 = localStorage.getItem('avatar2');

    if (player1 && player2 && avatar1 && avatar2) {
        document.getElementById('player1-name').textContent = player1;
        document.getElementById('player2-name').textContent = player2;
        document.getElementById('player1-avatar').src = avatar1;
        document.getElementById('player2-avatar').src = avatar2;
    } else {
        console.error('Player data is missing in localStorage.');
    }

    fetch('/checkers/save-players', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            player1: player1,
            player2: player2,
            avatar1: avatar1,
            avatar2: avatar2
        })
    })
        .then(response => {
            if (response.ok) {
                console.log("Players saved successfully");
            } else {
                console.error("Failed to save players");
            }
        })
        .catch(error => {
            console.error("Error saving players:", error);
        });
}

function updateActivePlayer() {
    const player1Block = document.getElementById('player1-block');
    const player2Block = document.getElementById('player2-block');

    if (!player1Block || !player2Block) {
        console.error('Один из элементов для отображения активного игрока не найден.');
        return;
    }

    player1Block.classList.remove('active-player');
    player2Block.classList.remove('active-player');

    if (isWhiteTurn) {
        player1Block.classList.add('active-player');
    } else {
        player2Block.classList.add('active-player');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    updatePlayerInfo();
    updateActivePlayer();

    if (typeof isWhiteTurnFromServer !== 'undefined') {
        isWhiteTurn = isWhiteTurnFromServer;
    }

    if (gameOverFromServer) {
        const modal = document.getElementById("gameOverModal");
        const winnerText = document.getElementById("winnerModal");
        const winnerAvatar = document.getElementById("winnerAvatar");

        if (modal) {
            modal.style.display = "block";
        }
        if (winnerText) {
            winnerText.textContent = winnerFromServer;
        }

        const avatar1 = localStorage.getItem("avatar1");
        const avatar2 = localStorage.getItem("avatar2");
        const player1 = localStorage.getItem("player1");

        if (winnerFromServer === player1 && avatar1) {
            winnerAvatar.src = avatar1;
        } else if (avatar2) {
            winnerAvatar.src = avatar2;
        }
    }
});
function closeModal() {
    document.getElementById("gameOverModal").style.display = "none";
}