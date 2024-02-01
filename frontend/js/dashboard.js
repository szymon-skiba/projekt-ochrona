document.addEventListener('DOMContentLoaded', () => {

    ping()
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
