document.addEventListener('DOMContentLoaded', () => {
    ping();
    const statusToken = localStorage.getItem('statusToken');
    fetch('/api/v1/user/sensitiveData', {
        credentials: 'include',
        headers: {
            'Authorization': `Bearer ${statusToken}`
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('creditCardNumber').textContent = `Credit Card Number: ${DOMPurify.sanitize(data.creditCardNumber)}`;
            document.getElementById('idCardNumber').textContent = `ID Card Number: ${DOMPurify.sanitize(data.idCardNumber)}`;
            document.getElementById('userName').textContent = `Name: ${DOMPurify.sanitize(data.userDto.firstname)} ${DOMPurify.sanitize(data.userDto.lastname)}`;
            document.getElementById('userAddress').textContent = `Address: ${DOMPurify.sanitize(data.userDto.address) || 'N/A'}`;
            document.getElementById('accountNumber').textContent = `Account Number: ${DOMPurify.sanitize(data.userDto.accountNumber)}`;
            document.getElementById('accountBalance').textContent = `Account Balance: $${DOMPurify.sanitize(data.userDto.accountBalance.toFixed(2))}`;
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