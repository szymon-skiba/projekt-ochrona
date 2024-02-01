package bank.app.demo.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private LocalDateTime date;

    @Nullable
    private String receiverAccount;

    @Nullable
    private String senderAccount;

    private BigDecimal amount;

    private String title;

    @Nullable
    private String receiverName;

    @Nullable
    private String receiverAddress;
}
