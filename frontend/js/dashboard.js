document.addEventListener('DOMContentLoaded', () => {

    ping()
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
