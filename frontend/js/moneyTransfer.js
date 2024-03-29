document.addEventListener('DOMContentLoaded', () => {

    ping();

    const transferForm = document.getElementById('transferForm');

    transferForm.addEventListener('submit', event => {
        event.preventDefault();

        const receiverAccount =  DOMPurify.sanitize(document.getElementById('receiverAccount').value);
        const amount =  DOMPurify.sanitize(document.getElementById('amount').value);
        const title =  DOMPurify.sanitize(document.getElementById('title').value);
        const receiverName =  DOMPurify.sanitize(document.getElementById('receiverName').value);
        const city =  DOMPurify.sanitize(document.getElementById('city').value);
        const street =  DOMPurify.sanitize(document.getElementById('street').value);
        const streetNumber =  DOMPurify.sanitize(document.getElementById('streetNumber').value);

        if (!validateTransferData(receiverAccount, amount, receiverName, city, street, streetNumber)) {
            alert('Invalid input. Please check your entries and try again.');
            return;
        }

        const receiverAddress = `${city}, ${street} ${streetNumber}`;
        const transferData = { receiverAccount, amount, title, receiverName, receiverAddress };
        const statusToken = localStorage.getItem('statusToken');

        fetch('/api/v1/transaction', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${statusToken}` 
            },
            body: JSON.stringify(transferData),
            credentials: 'include'
        }).then(response => {
            if (response.ok) {
                alert('Transfer successful!');
                transferForm.reset();
            } else {
                alert('Transfer failed!');
            }
        }).catch(error => console.error('Error:', error));
    });

    function validateTransferData(account, amount, name, city, street, streetNumber) {
        const accountRegex = /^\d{9}$/;
        const amountRegex = /^(0|([1-9][0-9]{0,8}))(\.[0-9]{1,2})?$/;
        const nameRegex = /^[a-zA-Z\s]+$/;
        const cityStreetRegex = /^[a-zA-Z\s]+$/;
        const streetNumberRegex = /^[0-9]{1,4}$/;

        return accountRegex.test(account) &&
               amountRegex.test(amount) &&
               nameRegex.test(name) &&
               cityStreetRegex.test(city) &&
               cityStreetRegex.test(street) &&
               streetNumberRegex.test(streetNumber);
    }

    function ping(){
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
