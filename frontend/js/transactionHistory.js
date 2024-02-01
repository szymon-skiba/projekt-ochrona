document.addEventListener('DOMContentLoaded', () => {
    ping()
    fetch('/api/v1/user/transactionHistory', { credentials: 'include' })
        .then(response => response.json())
        .then(history => {
            const receivedTransactions = history.receivedTransactions.map(trx =>
                `<li>${trx.date} - ${trx.senderAccount}: ${trx.amount}, ${trx.title}</li>`
            ).join('');
            document.getElementById('receivedTransactions').innerHTML = receivedTransactions;

            const sentTransactions = history.sentTransactions.map(trx =>
                `<li>${trx.date} - ${trx.receiverAccount}: ${trx.amount}, ${trx.title}, 
                ${trx.receiverName}, ${trx.receiverAddress}</li>`
            ).join('');
            document.getElementById('sentTransactions').innerHTML = sentTransactions;
        }).catch(error => console.error('Error:', error));


    function ping() {
        fetch('/api/v1/user/ping', {
            method: 'GET',
            credentials: 'include'
        }).then(response => {
            if (!response.ok) {
                window.location.href = '/';
            }
        }).catch(error => window.location.href = '/');
    }
});
