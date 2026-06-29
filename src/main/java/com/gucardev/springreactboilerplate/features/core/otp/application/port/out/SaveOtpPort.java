package com.gucardev.springreactboilerplate.features.core.otp.application.port.out;

import com.gucardev.springreactboilerplate.features.core.otp.domain.model.Otp;

/**
 * Output port: persist an OTP (insert or update) and return the stored state, including any generated
 * id and audit metadata.
 */
public interface SaveOtpPort {

    Otp save(Otp otp);
}
