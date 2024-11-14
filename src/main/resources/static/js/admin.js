$(document).ready(()=> {
    setupAjax();

    getUserInfo().then((userinfo) => {
        console.log(userinfo)
    }).catch((error) => {
        console.log('err', error)
    });

    checkToken().then(() => {

    $.ajax({
        url:'/admin',
        type:'GET',
        success: function (response) {
            console.log('res :: ', response)
        },
        error: function (xhr) {
            if(xhr.status === 401){
                handleTokenExpiration();
            }else if (xhr.status === 403) {
                window.location.href='/access-denied';
            }else{
                alert("Unexpected error")
            }
            }
    });
    });

});