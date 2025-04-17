let firstCoordinates = null;
let possibleMoves = [];

function selectTile(row, col) {
    const selectedTile = document.getElementById(`tile-${row}-${col}`);
    const piece = selectedTile.querySelector('.piece-checker, .piece-king');

    const isSameTileSelected = firstCoordinates &&
        firstCoordinates.row === row && firstCoordinates.col === col;

    // Повторный выбор той же шашки — сброс
    if (isSameTileSelected) {
        clearSelection();
        return;
    }

    // Если уже была выбрана шашка — пытаемся сходить
    if (firstCoordinates) {
        if (possibleMoves.some(move => move.row === row && move.col === col)) {
            // Совершаем ход
            const url = `/checkers?fr=${firstCoordinates.row}&fc=${firstCoordinates.col}&tr=${row}&tc=${col}`;
            window.location.href = url;
            return;
        } else {
            // Выбор другой шашки или некорректной клетки
            clearSelection();
        }
    }

    // Если клик по пустой клетке — просто сброс
    if (!piece) {
        return;
    }

    // Проверка цвета шашки
    const isWhiteTurn = document.getElementById('player-turn').classList.contains('white-turn');
    const isWhitePiece = piece.alt.includes('white');

    if ((isWhiteTurn && !isWhitePiece) || (!isWhiteTurn && isWhitePiece)) {
        return;
    }

    // Получаем возможные ходы (синхронный xhr остаётся, можно позже заменить на async)
    const moves = getPossibleMoves(row, col);

    // Если нет доступных ходов — ничего не выделяем
    if (moves.length === 0) {
        return;
    }

    // Устанавливаем выбор и подсвечиваем
    firstCoordinates = { row, col };
    possibleMoves = moves;
    selectedTile.classList.add('selected');
    highlightPossibleMoves(moves);
}

function getPossibleMoves(row, col) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/checkers/moves?row=${row}&col=${col}`, false);
    xhr.send();

    if (xhr.status === 200) {
        try {
            const moves = JSON.parse(xhr.responseText);
            return moves.map(move => ({ row: move[0], col: move[1] }));
        } catch (e) {
            console.error('Ошибка при разборе JSON:', e);
            return [];
        }
    }
    return [];
}

function highlightPossibleMoves(moves) {
    moves.forEach(move => {
        const tile = document.getElementById(`tile-${move.row}-${move.col}`);
        tile.classList.add('possible-move');
    });
}

function clearSelection() {
    // Убираем выделение и подсветку
    document.querySelectorAll('.tile').forEach(tile => {
        tile.classList.remove('selected');
        tile.classList.remove('possible-move');
    });

    firstCoordinates = null;
    possibleMoves = [];
}