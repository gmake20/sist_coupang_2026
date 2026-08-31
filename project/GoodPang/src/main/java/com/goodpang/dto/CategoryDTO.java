package com.goodpang.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private long categoryNo;
    private String categoryName;
    private Long parentCategoryNo;
    private int categoryLevel;
    private String imgUrl;
}
