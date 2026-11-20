package com.project.finance_system.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        String mounth,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance,
        String currency
) {
    
}
