package com.optiflow.booking.application.command;

import com.optiflow.booking.domain.vo.EmailAddress;

public record LoginCommand(EmailAddress email, String password) {
}
