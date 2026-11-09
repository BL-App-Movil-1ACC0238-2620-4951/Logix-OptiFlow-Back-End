package com.optiflow.platform.notification.interfaces.rest.assemblers;

import com.optiflow.platform.notification.domain.model.commands.AnswerSatisfactionSurveyCommand;
import com.optiflow.platform.notification.interfaces.rest.resources.AnswerSatisfactionSurveyRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class FromAnswerSatisfactionSurveyRequestAssembler {

  public AnswerSatisfactionSurveyCommand toCommand(
      UUID notificationId, AnswerSatisfactionSurveyRequest request) {
    return new AnswerSatisfactionSurveyCommand(
        notificationId, request.rating(), request.comment());
  }
}
