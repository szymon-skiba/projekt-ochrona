document.addEventListener('DOMContentLoaded', () => {
    const resetForm = document.getElementById('resetForm');
    const token = new URLSearchParams(window.location.search).get('token');

    if (!validateToken(token)) {
        displayMessageAndRedirect();
        return;
    }

    resetForm.addEventListener('submit', event => {
        event.preventDefault();
        const password = document.getElementById('newPassword').value;

        if (!validatePassword(password)) {
            alert('Password must be at least 8 characters long, include at least one number and one uppercase letter.');
            return;
        }

        fetch('/api/v1/auth/passwordreset', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token, password }),
        }).then(response => {
            if (response.ok) {
                alert('Password reset successfully. Please log in with your new password.');
                window.location.href = '';
            } else {
                alert('Password reset failed. Please try again or contact support.');
            }
        }).catch(error => console.error('Error:', error));
    });

    function validateToken(token) {
        return /^[A-Za-z0-9-]{36}$/.test(token);
    }

    function validatePassword(password) {
        return /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/.test(password);
    }

    function displayMessageAndRedirect() {
        alert('If the provided information is correct, your password has been reset. Please log in with your new password.');
        window.location.href = '';
    }
});
