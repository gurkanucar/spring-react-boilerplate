package com.gucardev.springreactboilerplate.features.example.adapter.in.web.dto;

import com.gucardev.springreactboilerplate.features.shared.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Example resource with its audit metadata.")
public class ExampleResponseDto extends BaseDto {

    @Schema(description = "Identifier", example = "1")
    private Long id;

    @Schema(description = "Display name", example = "Demo example")
    private String name;

    @Schema(description = "Free-text description", example = "Anything you like")
    private String description;

    @Schema(description = "Whether the example is active", example = "true")
    private Boolean active;
}
