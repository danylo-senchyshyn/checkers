let fromRow, fromCol;

document.querySelectorAll('.tile-inner img').forEach(img => {
    img.addEventListener('dragstart', event => {
        fromRow = event.target.parentElement.getAttribute('data-row');
        fromCol = event.target.parentElement.getAttribute('data-col');
        event.dataTransfer.setData('text/plain', '');
    });

    img.addEventListener('dragend', event => {
        event.preventDefault();
    });
});

document.querySelectorAll('.tile-inner').forEach(tile => {
    tile.addEventListener('dragover', event => {
        event.preventDefault();
    });

    tile.addEventListener('drop', event => {
        event.preventDefault();

        const toRow = event.currentTarget.getAttribute('data-row');
        const toCol = event.currentTarget.getAttribute('data-col');

        sendMove(fromRow, fromCol, toRow, toCol);

        fromRow = null;
        fromCol = null;
    });
});

function sendMove(fromRow, fromCol, toRow, toCol) {
    console.log(`Sending move from ${fromRow},${fromCol} to ${toRow},${toCol}`);

    const url = `/checkers?fromRow=${fromRow}&fromCol=${fromCol}&toRow=${toRow}&toCol=${toCol}`;
    console.log(url);
    fetch(url)
        .then(response => {
            console.log(response);
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                //window.location.reload();
            }
        })
        .catch(error => {
            console.error("Move failed", error);
        });
}