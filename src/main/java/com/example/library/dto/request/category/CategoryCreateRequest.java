package com.example.library.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryCreateRequest {
    @NotBlank(message = "Tên thể loại không được để trống.")
    @Size(max = 100, message = "Tên thể loại tối đa 100 ký tự.")
    private String name;
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự.")
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
