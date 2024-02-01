package bank.app.demo.model.dto;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveDataDto {
    private String creditCardNumber;
    private String idCardNumber;
    @Nullable
    private UserDto userDto;
}
