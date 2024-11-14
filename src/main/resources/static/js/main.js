$(document).ready(()=>{
    setupAjax();
    getUserInfo().then((userinfo) => {
        console.log(userinfo)
    }).catch((error) => {
        console.log('err', error)

    });

    checkToken().then(() => {
        $('#logout-button').click((event) => {
            event.preventDefault();

            alert('hi')

            $.ajax({
                type: 'POST',
                url: '/logout',
                contentType: 'application/json; charset=utf-8',
                success: (response) => {
                    alert(response.message);
                    localStorage.removeItem('accessToken');
                    window.location.href = response.url;
                },
                error: (error) => {
                    console.log('logout 오류 발생 :: ', error);
                    alert('로그아웃 중 오류가 발생했습니다!');
                }

            });
        });
    });

    $('#user-button').click(()=>{
        window.location.href="/member/user";
    });

    $('#admin-button').click(()=>{
        window.location.href="/member/admin";
    });

    $('#all-button').click(()=>{
        window.location.href="/member/all";
    });
});