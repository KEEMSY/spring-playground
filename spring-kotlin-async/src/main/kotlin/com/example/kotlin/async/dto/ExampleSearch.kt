package com.example.kotlin.async.dto

/**
 * 검색을 위한 DTO
 * @property exampleTitle 검색할 제목
 * @property exampleDescription 검색할 설명

검색할 값이 입력되지 않는다면, 모든 데이터를 조회한다.

[ 앞으로 추가되면 좋을 사항 ]
- 페이징 처리를 위한 변수 추가: page, size
-

 */
data class ExampleSearch
    (
    val exampleTitle: String? = null,
    val exampleDescription: String? = null,
) {
}
