document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    }

    function handleLogout() {

        const statusToken = localStorage.getItem('statusToken');
        fetch('/api/v1/user/logout', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${statusToken}` 
            },
            credentials: 'include' 
        })
        .then(response => {
            if (response.ok) {
                localStorage.removeItem('statusToken');
                window.location.href = '/';
            } else {
                alert('Logout failed.');
            }
        })
        .catch(error => console.error('Error:', error));
    }
});