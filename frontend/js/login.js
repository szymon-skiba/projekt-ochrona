function sanitizeInput(input) {
    return input.replace(/<script.*?>.*?<\/script>/gi, '')
        .replace(/[<>]/g, function (tag) {
            const tagsToReplace = {
                '<': '&lt;',
                '>': '&gt;'
            };
            return tagsToReplace[tag] || tag;
        });
}

function validateEmail(email) {
    const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return re.test(String(email).toLowerCase());
}

document.addEventListener('DOMContentLoaded', () => {
    const usernameForm = document.getElementById('usernameForm');
    const passwordForm = document.getElementById('passwordForm');
    let requestedChars;
    let email;

    usernameForm.addEventListener('submit', event => {
        event.preventDefault();
        email = document.getElementById('username').value;

        if (!validateEmail(email)) {
            alert('Please enter a valid email');
            return;
        }
        email = sanitizeInput(email);

        fetch('/api/v1/auth/maskedLogin/letters', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email }),
            credentials: 'include'
        })
            .then(response => response.json())
            .then(data => {
                requestedChars = data;
                if(requestedChars==0){
                    alert('Your account is blocked. Try password reset or contact your local bank!');
                    window.location.href = '/';
                }
                document.getElementById('passwordInstructions').textContent = `Enter the ${requestedChars} characters of your password.`;
                usernameForm.style.display = 'none';
                passwordForm.style.display = 'block';
            }).catch(error => console.error('Error:', error));
    });

    passwordForm.addEventListener('submit', event => {

        event.preventDefault();
        console.log(email)
        let password = document.getElementById('partialPassword').value;

        password = sanitizeInput(password)

        fetch('/api/v1/auth/maskedLogin', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
            credentials: 'include'
        }).then(response => response.json())
            .then(data => {
                if (data.success) {
                    localStorage.setItem('statusToken', data.data.statusToken);
                    window.location.href = 'dashboard.html';
                } else {
                    alert(data.message || 'Login failed!');
                    window.location.href = '/';
                }
            }).catch(error => {
                console.error('Error:', error);
                alert('Login failed!');
                window.location.href = '/';
            });
    });
});

