package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the ThingyData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ThingyData", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class ThingyData implements Model {
  public static final QueryField ID = field("ThingyData", "id");
  public static final QueryField THINGY_ACC_X = field("ThingyData", "thingy_acc_x");
  public static final QueryField THINGY_ACC_Y = field("ThingyData", "thingy_acc_y");
  public static final QueryField THINGY_ACC_Z = field("ThingyData", "thingy_acc_z");
  public static final QueryField THINGY_DATA_TIME = field("ThingyData", "thingy_dataTime");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Float") Double thingy_acc_x;
  private final @ModelField(targetType="Float") Double thingy_acc_y;
  private final @ModelField(targetType="Float") Double thingy_acc_z;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime thingy_dataTime;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public Double getThingyAccX() {
      return thingy_acc_x;
  }
  
  public Double getThingyAccY() {
      return thingy_acc_y;
  }
  
  public Double getThingyAccZ() {
      return thingy_acc_z;
  }
  
  public Temporal.DateTime getThingyDataTime() {
      return thingy_dataTime;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private ThingyData(String id, Double thingy_acc_x, Double thingy_acc_y, Double thingy_acc_z, Temporal.DateTime thingy_dataTime) {
    this.id = id;
    this.thingy_acc_x = thingy_acc_x;
    this.thingy_acc_y = thingy_acc_y;
    this.thingy_acc_z = thingy_acc_z;
    this.thingy_dataTime = thingy_dataTime;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      ThingyData thingyData = (ThingyData) obj;
      return ObjectsCompat.equals(getId(), thingyData.getId()) &&
              ObjectsCompat.equals(getThingyAccX(), thingyData.getThingyAccX()) &&
              ObjectsCompat.equals(getThingyAccY(), thingyData.getThingyAccY()) &&
              ObjectsCompat.equals(getThingyAccZ(), thingyData.getThingyAccZ()) &&
              ObjectsCompat.equals(getThingyDataTime(), thingyData.getThingyDataTime()) &&
              ObjectsCompat.equals(getCreatedAt(), thingyData.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), thingyData.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getThingyAccX())
      .append(getThingyAccY())
      .append(getThingyAccZ())
      .append(getThingyDataTime())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ThingyData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("thingy_acc_x=" + String.valueOf(getThingyAccX()) + ", ")
      .append("thingy_acc_y=" + String.valueOf(getThingyAccY()) + ", ")
      .append("thingy_acc_z=" + String.valueOf(getThingyAccZ()) + ", ")
      .append("thingy_dataTime=" + String.valueOf(getThingyDataTime()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static ThingyData justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new ThingyData(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      thingy_acc_x,
      thingy_acc_y,
      thingy_acc_z,
      thingy_dataTime);
  }
  public interface BuildStep {
    ThingyData build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep thingyAccX(Double thingyAccX);
    BuildStep thingyAccY(Double thingyAccY);
    BuildStep thingyAccZ(Double thingyAccZ);
    BuildStep thingyDataTime(Temporal.DateTime thingyDataTime);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private Double thingy_acc_x;
    private Double thingy_acc_y;
    private Double thingy_acc_z;
    private Temporal.DateTime thingy_dataTime;
    @Override
     public ThingyData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ThingyData(
          id,
          thingy_acc_x,
          thingy_acc_y,
          thingy_acc_z,
          thingy_dataTime);
    }
    
    @Override
     public BuildStep thingyAccX(Double thingyAccX) {
        this.thingy_acc_x = thingyAccX;
        return this;
    }
    
    @Override
     public BuildStep thingyAccY(Double thingyAccY) {
        this.thingy_acc_y = thingyAccY;
        return this;
    }
    
    @Override
     public BuildStep thingyAccZ(Double thingyAccZ) {
        this.thingy_acc_z = thingyAccZ;
        return this;
    }
    
    @Override
     public BuildStep thingyDataTime(Temporal.DateTime thingyDataTime) {
        this.thingy_dataTime = thingyDataTime;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, Double thingyAccX, Double thingyAccY, Double thingyAccZ, Temporal.DateTime thingyDataTime) {
      super.id(id);
      super.thingyAccX(thingyAccX)
        .thingyAccY(thingyAccY)
        .thingyAccZ(thingyAccZ)
        .thingyDataTime(thingyDataTime);
    }
    
    @Override
     public CopyOfBuilder thingyAccX(Double thingyAccX) {
      return (CopyOfBuilder) super.thingyAccX(thingyAccX);
    }
    
    @Override
     public CopyOfBuilder thingyAccY(Double thingyAccY) {
      return (CopyOfBuilder) super.thingyAccY(thingyAccY);
    }
    
    @Override
     public CopyOfBuilder thingyAccZ(Double thingyAccZ) {
      return (CopyOfBuilder) super.thingyAccZ(thingyAccZ);
    }
    
    @Override
     public CopyOfBuilder thingyDataTime(Temporal.DateTime thingyDataTime) {
      return (CopyOfBuilder) super.thingyDataTime(thingyDataTime);
    }
  }
  
}
