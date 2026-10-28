package com.swaraj.trading.Model;

import com.swaraj.trading.Domain.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;
    private VerificationType sendTo;

}
