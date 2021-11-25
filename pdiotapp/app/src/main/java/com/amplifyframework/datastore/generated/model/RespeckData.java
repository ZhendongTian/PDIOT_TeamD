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

/** This is an auto generated class representing the RespeckData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "RespeckData", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class RespeckData implements Model {
  public static final QueryField ID = field("RespeckData", "id");
  public static final QueryField RES_ACC_X = field("RespeckData", "res_acc_x");
  public static final QueryField RES_ACC_Y = field("RespeckData", "res_acc_y");
  public static final QueryField RES_ACC_Z = field("RespeckData", "res_acc_z");
  public static final QueryField RES_GYRO_X = field("RespeckData", "res_gyro_x");
  public static final QueryField RES_GYRO_Y = field("RespeckData", "res_gyro_y");
  public static final QueryField RES_GYRO_Z = field("RespeckData", "res_gyro_z");
  public static final QueryField RES_DATA_TIME = field("RespeckData", "res_dataTime");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Float", isRequired = true) Double res_acc_x;
  private final @ModelField(targetType="Float") Double res_acc_y;
  private final @ModelField(targetType="Float") Double res_acc_z;
  private final @ModelField(targetType="Float") Double res_gyro_x;
  private final @ModelField(targetType="Float") Double res_gyro_y;
  private final @ModelField(targetType="Float") Double res_gyro_z;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime res_dataTime;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public Double getResAccX() {
      return res_acc_x;
  }
  
  public Double getResAccY() {
      return res_acc_y;
  }
  
  public Double getResAccZ() {
      return res_acc_z;
  }
  
  public Double getResGyroX() {
      return res_gyro_x;
  }
  
  public Double getResGyroY() {
      return res_gyro_y;
  }
  
  public Double getResGyroZ() {
      return res_gyro_z;
  }
  
  public Temporal.DateTime getResDataTime() {
      return res_dataTime;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private RespeckData(String id, Double res_acc_x, Double res_acc_y, Double res_acc_z, Double res_gyro_x, Double res_gyro_y, Double res_gyro_z, Temporal.DateTime res_dataTime) {
    this.id = id;
    this.res_acc_x = res_acc_x;
    this.res_acc_y = res_acc_y;
    this.res_acc_z = res_acc_z;
    this.res_gyro_x = res_gyro_x;
    this.res_gyro_y = res_gyro_y;
    this.res_gyro_z = res_gyro_z;
    this.res_dataTime = res_dataTime;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      RespeckData respeckData = (RespeckData) obj;
      return ObjectsCompat.equals(getId(), respeckData.getId()) &&
              ObjectsCompat.equals(getResAccX(), respeckData.getResAccX()) &&
              ObjectsCompat.equals(getResAccY(), respeckData.getResAccY()) &&
              ObjectsCompat.equals(getResAccZ(), respeckData.getResAccZ()) &&
              ObjectsCompat.equals(getResGyroX(), respeckData.getResGyroX()) &&
              ObjectsCompat.equals(getResGyroY(), respeckData.getResGyroY()) &&
              ObjectsCompat.equals(getResGyroZ(), respeckData.getResGyroZ()) &&
              ObjectsCompat.equals(getResDataTime(), respeckData.getResDataTime()) &&
              ObjectsCompat.equals(getCreatedAt(), respeckData.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), respeckData.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getResAccX())
      .append(getResAccY())
      .append(getResAccZ())
      .append(getResGyroX())
      .append(getResGyroY())
      .append(getResGyroZ())
      .append(getResDataTime())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("RespeckData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("res_acc_x=" + String.valueOf(getResAccX()) + ", ")
      .append("res_acc_y=" + String.valueOf(getResAccY()) + ", ")
      .append("res_acc_z=" + String.valueOf(getResAccZ()) + ", ")
      .append("res_gyro_x=" + String.valueOf(getResGyroX()) + ", ")
      .append("res_gyro_y=" + String.valueOf(getResGyroY()) + ", ")
      .append("res_gyro_z=" + String.valueOf(getResGyroZ()) + ", ")
      .append("res_dataTime=" + String.valueOf(getResDataTime()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static ResAccXStep builder() {
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
  public static RespeckData justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new RespeckData(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      res_acc_x,
      res_acc_y,
      res_acc_z,
      res_gyro_x,
      res_gyro_y,
      res_gyro_z,
      res_dataTime);
  }
  public interface ResAccXStep {
    BuildStep resAccX(Double resAccX);
  }
  

  public interface BuildStep {
    RespeckData build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep resAccY(Double resAccY);
    BuildStep resAccZ(Double resAccZ);
    BuildStep resGyroX(Double resGyroX);
    BuildStep resGyroY(Double resGyroY);
    BuildStep resGyroZ(Double resGyroZ);
    BuildStep resDataTime(Temporal.DateTime resDataTime);
  }
  

  public static class Builder implements ResAccXStep, BuildStep {
    private String id;
    private Double res_acc_x;
    private Double res_acc_y;
    private Double res_acc_z;
    private Double res_gyro_x;
    private Double res_gyro_y;
    private Double res_gyro_z;
    private Temporal.DateTime res_dataTime;
    @Override
     public RespeckData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new RespeckData(
          id,
          res_acc_x,
          res_acc_y,
          res_acc_z,
          res_gyro_x,
          res_gyro_y,
          res_gyro_z,
          res_dataTime);
    }
    
    @Override
     public BuildStep resAccX(Double resAccX) {
        Objects.requireNonNull(resAccX);
        this.res_acc_x = resAccX;
        return this;
    }
    
    @Override
     public BuildStep resAccY(Double resAccY) {
        this.res_acc_y = resAccY;
        return this;
    }
    
    @Override
     public BuildStep resAccZ(Double resAccZ) {
        this.res_acc_z = resAccZ;
        return this;
    }
    
    @Override
     public BuildStep resGyroX(Double resGyroX) {
        this.res_gyro_x = resGyroX;
        return this;
    }
    
    @Override
     public BuildStep resGyroY(Double resGyroY) {
        this.res_gyro_y = resGyroY;
        return this;
    }
    
    @Override
     public BuildStep resGyroZ(Double resGyroZ) {
        this.res_gyro_z = resGyroZ;
        return this;
    }
    
    @Override
     public BuildStep resDataTime(Temporal.DateTime resDataTime) {
        this.res_dataTime = resDataTime;
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
    private CopyOfBuilder(String id, Double resAccX, Double resAccY, Double resAccZ, Double resGyroX, Double resGyroY, Double resGyroZ, Temporal.DateTime resDataTime) {
      super.id(id);
      super.resAccX(resAccX)
        .resAccY(resAccY)
        .resAccZ(resAccZ)
        .resGyroX(resGyroX)
        .resGyroY(resGyroY)
        .resGyroZ(resGyroZ)
        .resDataTime(resDataTime);
    }
    
    @Override
     public CopyOfBuilder resAccX(Double resAccX) {
      return (CopyOfBuilder) super.resAccX(resAccX);
    }
    
    @Override
     public CopyOfBuilder resAccY(Double resAccY) {
      return (CopyOfBuilder) super.resAccY(resAccY);
    }
    
    @Override
     public CopyOfBuilder resAccZ(Double resAccZ) {
      return (CopyOfBuilder) super.resAccZ(resAccZ);
    }
    
    @Override
     public CopyOfBuilder resGyroX(Double resGyroX) {
      return (CopyOfBuilder) super.resGyroX(resGyroX);
    }
    
    @Override
     public CopyOfBuilder resGyroY(Double resGyroY) {
      return (CopyOfBuilder) super.resGyroY(resGyroY);
    }
    
    @Override
     public CopyOfBuilder resGyroZ(Double resGyroZ) {
      return (CopyOfBuilder) super.resGyroZ(resGyroZ);
    }
    
    @Override
     public CopyOfBuilder resDataTime(Temporal.DateTime resDataTime) {
      return (CopyOfBuilder) super.resDataTime(resDataTime);
    }
  }
  
}
