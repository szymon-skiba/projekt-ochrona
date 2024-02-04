document.addEventListener('DOMContentLoaded', () => {
    const resetForm = document.getElementById('resetForm');

    resetForm.addEventListener('submit', event => {
        event.preventDefault();
        const email =  DOMPurify.sanitize(document.getElementById('email').value);

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
});
