$(document).ready(() => {
    $('#signup-button').click((event) => {
        event.preventDefault();

        $('#user_options-forms').removeClass('bounceRight').addClass('bounceLeft');

    });

    $('#sign-up').click((event)=>{
        event.preventDefault();

        let userId=$('#userId').val();
        let password=$('#password').val();
        let userName=$('#userName').val();

        let formData={
            userId : userId,
            password : password,
            userName : userName
        }


        $.ajax({
            url:"/join",
            type:"POST",
            dataType:'json',
            data: JSON.stringify(formData),
            contentType:'application/json; charset=utf-8',
            success: function (response) {
                console.log('response :: ', response)
                alert(response.message);
                window.location.href=response.url;
            },
            error: function(error) {
                // 실패 시 실행될 콜백 함수
                console.error('오류 발생:', error);
                alert('회원가입 중 오류가 발생했습니다.');
            }
        });
    })


    $('#login-button').click((event) => {
        event.preventDefault();
        $('#user_options-forms').removeClass('bounceLeft').addClass('bounceRight');

    });

    $('#login').click((event)=>{
        event.preventDefault();


        let userId=$('#LoginUserId').val();
        let password=$('#LoginPassword').val();

        let formData={
            userId : userId,
            password : password,
        }

        console.log('formData :: ', formData);

        $.ajax({
            url:"/login",
            type:"POST",
            dataType:'json',
            data: JSON.stringify(formData),
            contentType:'application/json; charset=utf-8',
            success: function (response) {
                console.log('response ::  ',response)
                if(response.loggedIn){
                    alert(response.message)
                    localStorage.setItem('accessToken', response.accessToken);
                    console.log(response.accessToken);
                    window.location.href = response.url;
                }
            },
            error: function(error) {
                // 실패 시 실행될 콜백 함수
                console.error('오류 발생:', error);
                alert('로그인 중 오류가 발생했습니다.');
                // window.location.href="/main/join";
            }
        });
    })
});
