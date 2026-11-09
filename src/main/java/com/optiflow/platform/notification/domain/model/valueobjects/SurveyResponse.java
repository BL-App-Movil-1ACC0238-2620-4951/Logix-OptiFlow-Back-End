package com.optiflow.platform.notification.domain.model.valueobjects;

import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.time.Instant;

/** Answer of a satisfaction survey: a rating from 1 to 5 and an optional comment. */
public record SurveyResponse(int rating, String comment, Instant answeredAt) {

  private static final int MAX_COMMENT_LENGTH = 500;

  public SurveyResponse {
    if (rating < 1 || rating > 5) {
      throw new DomainException("The survey rating must be between 1 and 5.", 400);
    }
    comment = comment == null || comment.isBlank() ? null : comment.trim();
    if (comment != null && comment.length() > MAX_COMMENT_LENGTH) {
      throw new DomainException("The survey comment must have at most 500 characters.", 400);
    }
    if (answeredAt == null) {
      throw new DomainException("The survey answer date is required.", 400);
    }
  }
}
