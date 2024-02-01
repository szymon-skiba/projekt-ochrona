document.addEventListener('DOMContentLoaded', () => {
    ping();
    fetch('/api/v1/user/sensitiveData', { credentials: 'include' })
        .then(response => response.json())
        .then(data => {
            document.getElementById('creditCardNumber').textContent = `Credit Card Number: ${data.creditCardNumber}`;
            document.getElementById('idCardNumber').textContent = `ID Card Number: ${data.idCardNumber}`;
            document.getElementById('userName').textContent = `Name: ${data.userDto.firstname} ${data.userDto.lastname}`;
            document.getElementById('userAddress').textContent = `Address: ${data.userDto.address || 'N/A'}`;
            document.getElementById('accountNumber').textContent = `Account Number: ${data.userDto.accountNumber}`;
            document.getElementById('accountBalance').textContent = `Account Balance: $${data.userDto.accountBalance.toFixed(2)}`;
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