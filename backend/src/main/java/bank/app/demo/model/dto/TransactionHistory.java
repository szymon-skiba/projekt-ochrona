package bank.app.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TransactionHistory {
    private List<TransactionDto> receivedTransactions;
    private List<TransactionDto> sentTransactions;
}
