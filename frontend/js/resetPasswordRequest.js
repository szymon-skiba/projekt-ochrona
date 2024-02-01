document.addEventListener('DOMContentLoaded', () => {
    const resetForm = document.getElementById('resetForm');

    resetForm.addEventListener('submit', event => {
        event.preventDefault();
        const email = document.getElementById('email').value;

        if (!validateEmail(email)) {
            alert('Please enter a valid username.');
            return;
        }

        fetch('/api/v1/auth/passwordreset', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email }),
        }).then(response => {
            if (response.ok) {
                let emialtext = response.json()
                emialtext.then(data => {
                    console.log(data)
                    alert('Reset successful. Check your email !!\n' + data.emailtext);
                })
            } else {
                alert('Reset failed!');
            }
        }).catch(error => console.error('Error:', error));
    });

    function validateEmail(email) {
        const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        return re.test(String(email).toLowerCase());
    }

    function displayMessageAndRedirect() {
        alert('If the provided information is correct, your password has been reset. Please log in with your new password.');
        window.location.href = 'index.html';
    }
});
