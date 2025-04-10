document.addEventListener("DOMContentLoaded", () => {
    let draggedPiece = null;
    let sourceTile = null;

    // Сделать все шашки перетаскиваемыми
    document.querySelectorAll(".piece").forEach(piece => {
        piece.setAttribute("draggable", "true");

        piece.addEventListener("dragstart", (e) => {
            draggedPiece = piece;
            sourceTile = piece.closest(".tile");  // Важно, чтобы это была ближайшая .tile
            sourceTile.classList.add("dragging");  // Добавляем класс для полупрозрачности
            setTimeout(() => {
                piece.style.visibility = "hidden"; // скрыть оригинал при перетаскивании
            }, 0);
        });

        piece.addEventListener("dragend", (e) => {
            setTimeout(() => {
                draggedPiece.style.display = "block"; // показать снова
                draggedPiece = null;
                sourceTile.classList.remove("dragging");  // Убираем класс
                sourceTile = null;
            }, 0);
        });
    });

    // Обработать зоны дропа
    document.querySelectorAll(".tile").forEach(tile => {
        tile.addEventListener("dragover", (e) => {
            e.preventDefault(); // Разрешить дроп
        });

        tile.addEventListener("drop", (e) => {
            e.preventDefault();
            if (!draggedPiece || !sourceTile || tile === sourceTile) return;  // Убедиться, что шашка перемещается в другую клетку

            // Удалить с исходной клетки
            sourceTile.removeChild(draggedPiece);

            // Удалить шашку, если она уже есть в новой клетке
            const existing = tile.querySelector(".piece");
            if (existing) tile.removeChild(existing);

            // Добавить перемещённую шашку
            tile.appendChild(draggedPiece);
        });
    });
});