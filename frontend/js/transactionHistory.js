document.addEventListener('DOMContentLoaded', () => {
    ping()
    const statusToken = localStorage.getItem('statusToken');
    fetch('/api/v1/user/transactionHistory', {
        credentials: 'include',
        headers: {
            'Authorization': `Bearer ${statusToken}`
        }
    })
        .then(response => response.json())
        .then(history => {
            const receivedTransactions = history.receivedTransactions.map(trx =>
                `<li>${DOMPurify.sanitize(trx.date)} - ${DOMPurify.sanitize(trx.senderAccount)}: ${DOMPurify.sanitize(trx.amount)}, ${DOMPurify.sanitize(trx.title)}</li>`
            ).join('');
            document.getElementById('receivedTransactions').innerHTML = receivedTransactions;

            const sentTransactions = history.sentTransactions.map(trx =>
                `<li>${DOMPurify.sanitize(trx.date)} - ${DOMPurify.sanitize(trx.receiverAccount)}: ${DOMPurify.sanitize(trx.amount)}, ${DOMPurify.sanitize(trx.title)}, 
                ${DOMPurify.sanitize(trx.receiverName)}, ${DOMPurify.sanitize(trx.receiverAddress)}</li>`
            ).join('');
            document.getElementById('sentTransactions').innerHTML = sentTransactions;
        }).catch(error => console.error('Error:', error));


    function ping() {
        const statusToken = localStorage.getItem('statusToken');
        fetch('/api/v1/user/ping', {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Authorization': `Bearer ${statusToken}`
            }
        }).then(response => {
            if (!response.ok) {
                window.location.href = '/';
            }
        }).catch(error => window.location.href = '/');
    }
});
