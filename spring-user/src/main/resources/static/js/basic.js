const host = 'http://' + window.location.host;
const BEARER_PREFIX = "Bearer ";
$(document).ready(function () {
    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', BEARER_PREFIX + auth);
        });
    } else {
        window.location.href = host + '/api/user/login-page';
        return;
    }

    $.ajax({
        type: 'GET',
        url: `/api/user/user-info`,
        contentType: 'application/json',
    }).done(function(data, textStatus, jqXHR) {
        const username = data.username;

        if (!username) {
            window.location.href = '/api/user/login-page';
            return;
        }
        $('#username').text(username);

    }).fail(function(jqXHR, textStatus, errorThrown) {
        if (jqXHR.status === 401) {
            // 만료된 토큰으로 인한 실패
            window.location.href = '/api/user/login-page';
        } else {
            logout();
        }
    });
});

function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', { path: '/' });
    window.location.href = host + "/api/user/login-page";
}

function getToken() {
    let auth = Cookies.get('Authorization');

    if (auth === undefined) {
        return '';
    }

    return auth;
}