package com.example.springuser.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

/*
@NotNull: null 불가
@NotEmpty: null, “” 불가
@NotBlank: null, “”. “ “ 불가
@Size: 문자 길이 측정
@Max: 최대값
@Min: 최소값
@Positive: 양수
@Negative: 음수
@Email: E-mail 형식
@Pattern: 정규 표현식


/*
// @ 기호를 확인. 기호 앞과 뒤 문자는 신경쓰지 않는다.
String regx1 = "^(.+)@(.+)$";

// @ 기호 앞에 오는 방식에 제한을 추가한다.
// A-Z, a-z, 0-9, ., _ 를 사용할 수 있다.
String regx2 = "^[A-Za-z0-9+_.-]+@(.+)$";

// 이메일 형식에 허용되는 문자를 모두 사용할 수 있다.
String regx3 = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
 */

@Getter
public class ValidateTestRequest {
    @NotBlank
    private String name;
    @Email
    // @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$") 으로 @Email 대체 가능
    private String email;
    @Positive(message = "양수만 가능합니다.")
    private int price;
    @Negative(message = "음수만 가능합니다.")
    private int discount;
    @Size(min=2, max=10)
    private String link;
    @Max(10)
    private int max;
    @Min(2)
    private int min;
}
