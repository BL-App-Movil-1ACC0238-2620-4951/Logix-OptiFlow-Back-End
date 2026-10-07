package com.optiflow.booking.application.command;

import com.optiflow.booking.domain.vo.EmailAddress;
import com.optiflow.booking.domain.vo.Name;
import com.optiflow.booking.domain.vo.PhoneNumber;

public record RegisterPatientCommand(
    Name name, EmailAddress email, PhoneNumber phone, String password) {
}
