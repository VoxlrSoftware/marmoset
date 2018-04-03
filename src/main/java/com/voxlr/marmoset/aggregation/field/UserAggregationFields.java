package com.voxlr.marmoset.aggregation.field;

import static com.voxlr.marmoset.util.MapUtils.KVPair.entry;
import static com.voxlr.marmoset.util.MapUtils.mapOf;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

public class UserAggregationFields {
  public static AggregationField field(UserField field) {
    return userAggregationFields.get(field);
  }

  @Getter
  public enum UserField implements AggFieldName {
    COMPANY_ID("companyId"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    TOTAL_CALLS("totalCalls"),
    TOTAL_CALL_TIME("totalCallTime"),
    TOTAL_CONVERSATIONS("totalConversations"),
    CALL_STRATEGY("callStrategy"),
    CUSTOMER_TALK_RATIO("customerTalkRatio"),
    CONVERSATION_RATIO("conversationRatio");

    static Map<String, UserField> userFields;

    static {
      userFields =
          Arrays.asList(UserField.values())
              .stream()
              .collect(Collectors.toMap(UserField::getName, Function.identity()));
    }

    public static List<UserField> getAll() {
      return new ArrayList<>(userFields.values());
    }

    @JsonCreator
    public static UserField fromString(String value) {
      return userFields.get(value);
    }

    private String name;

    UserField(String name) {
      this.name = name;
    }
  }

  private static final Map<UserField, AggregationField> userAggregationFields =
      mapOf(
          entry(
              UserField.COMPANY_ID,
              AggregationField.builder(UserField.COMPANY_ID.getName())
                  .ableToRollup(false).build()),
          entry(
              UserField.FIRST_NAME,
              AggregationField.builder(UserField.FIRST_NAME.getName())
                  .ableToRollup(false).build()),
          entry(
              UserField.LAST_NAME,
              AggregationField.builder(UserField.LAST_NAME.getName())
                  .ableToRollup(false).build()));
}
