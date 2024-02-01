package bank.app.demo.model.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private String receiverAccount;

    private BigDecimal amount;

    private String title;

    private String receiverName;

    private String receiverAddress;
}
