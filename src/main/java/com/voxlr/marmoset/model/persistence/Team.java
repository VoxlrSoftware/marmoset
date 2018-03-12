package com.voxlr.marmoset.model.persistence;

import com.voxlr.marmoset.model.CompanyScopedEntity;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "teams")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class Team extends AuditModel implements CompanyScopedEntity {

  @NotNull private String name;

  @NotNull @Indexed private String companyId;
}
