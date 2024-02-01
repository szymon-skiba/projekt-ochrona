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
public class ServiceResponse<T>{
        private T data;
        private Boolean success;
        private String message;
        @Nullable
        private String cookieToken;
}
