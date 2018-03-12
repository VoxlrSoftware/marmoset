package com.voxlr.marmoset.aggregation.field;

import com.voxlr.marmoset.aggregation.dto.AggregateResultDTO;
import com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.GroupOperationModifier;
import com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.ProjectOperationModifier;
import lombok.*;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;

import java.util.List;
import java.util.stream.Collectors;

import static com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.groupAverageModifier;
import static com.voxlr.marmoset.aggregation.field.AggregationOperationModifiers.projectFieldModifier;
import static com.voxlr.marmoset.util.ListUtils.reduce;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregationField {
  public static ProjectionOperation getFieldProjections(
      ProjectionOperation initial, List<AggregationField> fields) {
    return reduce(initial, fields, AggregationField::projectField);
  }

  public static GroupOperation getFieldGroups(
      GroupOperation initial, List<AggregationField> fields) {
    return reduce(initial, fields, AggregationField::groupField);
  }

  public static ProjectionOperation projectField(
      ProjectionOperation input, AggregationField field) {
    return field.project(input);
  }

  public static GroupOperation groupField(GroupOperation input, AggregationField field) {
    return field.group(input);
  }

  public static AggregateResultDTO getDefaults(List<AggregationField> fields) {
    return new AggregateResultDTO(
        fields
            .stream()
            .filter(AggregationField::isAbleToRollup)
            .collect(
                Collectors.toMap(
                    AggregationField::getFieldName, AggregationField::getDefaultValue)));
  }

  public static boolean isProjectable(List<AggregationField> fields) {
    return fields.stream().anyMatch(AggregationField::isProjectable);
  }

  public static boolean isGroupable(List<AggregationField> fields) {
    return fields.stream().anyMatch(AggregationField::isAbleToRollup);
  }

  public static AggregationFieldBuilder builder(String fieldName) {
    return new AggregationFieldBuilder().fieldName(fieldName).pathName(fieldName);
  }

  private String pathName;
  private String fieldName;

  @Builder.Default private boolean ableToRollup = true;

  @Builder.Default private boolean projectable = true;

  @Builder.Default private Object defaultValue = null;

  @Builder.Default private ProjectOperationModifier projectOperationModifier = projectFieldModifier;

  @Builder.Default private GroupOperationModifier groupOperationModifier = groupAverageModifier;

  public ProjectionOperation project(ProjectionOperation input) {
    return isProjectable()
        ? getProjectOperationModifier().project(input, this).as(getFieldName())
        : input;
  }

  public GroupOperation group(GroupOperation input) {
    return isAbleToRollup()
        ? getGroupOperationModifier().group(input, this).as(getFieldName())
        : input;
  }
}
