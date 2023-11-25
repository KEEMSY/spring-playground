package spring.playground.springdata.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListProductResponseDto {
    private List<ProductResponseDto> productResponseDtos;

}