package com.stela.car;

import java.math.BigDecimal;
import java.util.UUID;

public record CarResponse(
        UUID id,
        String regNumber,
        BigDecimal rentalPricePerDay,
        Brand brand,
        boolean isElectric
) {
}
