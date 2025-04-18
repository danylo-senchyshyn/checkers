// game.js
let firstCoordinates = null;
let possibleMoves = [];

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

    // Проверяем, чей сейчас ход
    const isWhiteTurn = document.getElementById('player-turn').classList.contains('white-turn');
    const isWhitePiece = piece.alt && piece.alt.includes('white');

    if ((isWhiteTurn && !isWhitePiece) || (!isWhiteTurn && isWhitePiece)) {
        console.warn(`Invalid turn: White turn=${isWhiteTurn}, Piece is white=${isWhitePiece}.`);
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

function executeMove(row, col) {
    const moveText = `${firstCoordinates.row},${firstCoordinates.col} → ${row},${col}`;
    addMoveToLog(moveText);

    switchTurn();

    const player1 = localStorage.getItem('player1');
    const player2 = localStorage.getItem('player2');
    const avatar1 = localStorage.getItem('avatar1');
    const avatar2 = localStorage.getItem('avatar2');

    if (!player1 || !player2 || !avatar1 || !avatar2) {
        console.error('Player or avatar information is missing.');
        return;
    }

    const url = `/checkers?fr=${firstCoordinates.row}&fc=${firstCoordinates.col}&tr=${row}&tc=${col}`
        + `&avatar1=${encodeURIComponent(avatar1)}&avatar2=${encodeURIComponent(avatar2)}`;
    window.location.href = url;
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

function addMoveToLog(moveText) {
    const moveList = document.getElementById('move-list');
    if (moveList) {
        const li = document.createElement('li');
        li.textContent = moveText;
        moveList.appendChild(li);
    } else {
        console.warn('Move list element not found.');
    }
}

function startNewGame(player1, player2, avatar1, avatar2) {
    localStorage.setItem('player1', player1);
    localStorage.setItem('player2', player2);
    localStorage.setItem('avatar1', `/images/avatars/${avatar1}`);
    localStorage.setItem('avatar2', `/images/avatars/${avatar2}`);

    window.location.href = '/checkers';
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
}

document.addEventListener('DOMContentLoaded', function () {
    updatePlayerInfo();
});

function updateActivePlayer() {
    const playerTurnElement = document.getElementById('player-turn');
    const player1Block = document.getElementById('player1-block');
    const player2Block = document.getElementById('player2-block');

    if (!playerTurnElement || !player1Block || !player2Block) {
        console.error('Один из элементов для отображения активного игрока не найден.');
        return;
    }

    const isWhiteTurn = playerTurnElement.classList.contains('white-turn');

    player1Block.classList.remove('active-player');
    player2Block.classList.remove('active-player');

    if (isWhiteTurn) {
        player1Block.classList.add('active-player');
    } else {
        player2Block.classList.add('active-player');
    }
}

document.addEventListener('DOMContentLoaded', function() {
    updateActivePlayer();
});

function switchTurn() {
    const playerTurnElement = document.getElementById('player-turn');
    playerTurnElement.classList.toggle('white-turn');

    updateActivePlayer();
}