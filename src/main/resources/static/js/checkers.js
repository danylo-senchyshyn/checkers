let firstCoordinates = null;
let possibleMoves = [];

function selectTile(row, col) {
    const selectedTile = document.getElementById(`tile-${row}-${col}`);
    const piece = selectedTile.querySelector('.piece-checker, .piece-king');

    // Если первая клетка уже выбрана, обрабатываем второй клик
    if (firstCoordinates) {
        // Проверяем, что вторая клетка входит в возможные ходы
        if (possibleMoves.some(move => move.row === row && move.col === col)) {
            // Убираем выделение с предыдущей клетки
            const previousTile = document.getElementById(`tile-${firstCoordinates.row}-${firstCoordinates.col}`);
            previousTile.classList.remove('selected');

            // Убираем подсветку возможных ходов
            clearPossibleMoves();

            // Формируем URL для вызова метода move
            const url = `/checkers?fr=${firstCoordinates.row}&fc=${firstCoordinates.col}&tr=${row}&tc=${col}`;
            console.log(`Переход по URL: ${url}`);
            window.location.href = url; // Перенаправляем на URL
            firstCoordinates = null; // Сбрасываем первую клетку
            return;
        } else {
            console.log('Нельзя выбрать эту клетку.');
            return; // Преры��аем выполнение, если клетка не входит в возможные ходы
        }
    }

    // Проверяем, что клетка содержит шашку
    if (!piece) {
        console.log('Выбрана пустая клетка.');
        return; // Прерываем выполнение, если клетка пустая
    }

    // Проверяем, чей сейчас ход
    const isWhiteTurn = document.getElementById('player-turn').classList.contains('white-turn');

    // Проверяем, что выбранная шашка соответствует текущему ходу
    const isWhitePiece = piece.alt.includes('white');
    if ((isWhiteTurn && !isWhitePiece) || (!isWhiteTurn && isWhitePiece)) {
        console.log('Нельзя выбрать шашку противника.');
        return; // Прерываем выполнение, если шашка не соответствует текущему ходу
    }

    // Сохраняем первую выбранную клетку
    firstCoordinates = {row, col};
    selectedTile.classList.add('selected'); // Добавляем класс выделения
    console.log(`Первая клетка выбрана: (${row}, ${col})`);

    // Получаем возможные ходы и подсвечиваем их
    possibleMoves = getPossibleMoves(row, col);
    highlightPossibleMoves(possibleMoves);
}

function getPossibleMoves(row, col) {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/checkers/moves?row=${row}&col=${col}`, false);
    xhr.send();

    if (xhr.status === 200) {
        try {
            const moves = JSON.parse(xhr.responseText);
            console.log(`Полученные возможные ходы:`, moves);
            // Преобразуем массив массивов в массив объектов
            return moves.map(move => ({
                row: move[0],
                col: move[1]
            })).filter(move =>
                typeof move.row === 'number' &&
                typeof move.col === 'number'
            );
        } catch (e) {
            console.error('Ошибка при разборе ответа сервера:', e);
            return [];
        }
    } else {
        console.error('Ошибка при получении возможных ходов.');
        return [];
    }
}

function highlightPossibleMoves(moves) {
    if (moves.length === 0) {
        console.warn('Нет возможных ходов для подсветки.');
        return;
    }
    moves.forEach(move => {
        const tileId = `tile-${move.row}-${move.col}`;
        console.log(`Ищем клетку с ID: ${tileId}`);
        const tile = document.getElementById(tileId);
        if (tile) {
            tile.classList.add('possible-move');
        } else {
            console.warn(`Клетка с ID ${tileId} не найдена.`);
        }
    });
}

function clearPossibleMoves() {
    possibleMoves.forEach(move => {
        const tile = document.getElementById(`tile-${move.row}-${move.col}`);
        tile.classList.remove('possible-move'); // Убираем класс подсветки
    });
    possibleMoves = [];
}