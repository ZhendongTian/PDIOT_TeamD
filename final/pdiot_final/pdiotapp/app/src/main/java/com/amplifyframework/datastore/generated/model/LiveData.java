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

/** This is an auto generated class representing the LiveData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "LiveData", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class LiveData implements Model {
  public static final QueryField ID = field("LiveData", "id");
  public static final QueryField RES_ACC_X = field("LiveData", "res_acc_x");
  public static final QueryField RES_ACC_Y = field("LiveData", "res_acc_y");
  public static final QueryField RES_ACC_Z = field("LiveData", "res_acc_z");
  public static final QueryField RES_GYRO_X = field("LiveData", "res_gyro_x");
  public static final QueryField RES_GYRO_Y = field("LiveData", "res_gyro_y");
  public static final QueryField RES_GYRO_Z = field("LiveData", "res_gyro_z");
  public static final QueryField THINGY_ACC_X = field("LiveData", "thingy_acc_x");
  public static final QueryField THINGY_ACC_Y = field("LiveData", "thingy_acc_y");
  public static final QueryField THINGY_ACC_Z = field("LiveData", "thingy_acc_z");
  public static final QueryField DATA_TIME = field("LiveData", "dataTime");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Float", isRequired = true) Double res_acc_x;
  private final @ModelField(targetType="Float") Double res_acc_y;
  private final @ModelField(targetType="Float") Double res_acc_z;
  private final @ModelField(targetType="Float") Double res_gyro_x;
  private final @ModelField(targetType="Float") Double res_gyro_y;
  private final @ModelField(targetType="Float") Double res_gyro_z;
  private final @ModelField(targetType="Float") Double thingy_acc_x;
  private final @ModelField(targetType="Float") Double thingy_acc_y;
  private final @ModelField(targetType="Float") Double thingy_acc_z;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime dataTime;
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
  
  public Double getThingyAccX() {
      return thingy_acc_x;
  }
  
  public Double getThingyAccY() {
      return thingy_acc_y;
  }
  
  public Double getThingyAccZ() {
      return thingy_acc_z;
  }
  
  public Temporal.DateTime getDataTime() {
      return dataTime;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private LiveData(String id, Double res_acc_x, Double res_acc_y, Double res_acc_z, Double res_gyro_x, Double res_gyro_y, Double res_gyro_z, Double thingy_acc_x, Double thingy_acc_y, Double thingy_acc_z, Temporal.DateTime dataTime) {
    this.id = id;
    this.res_acc_x = res_acc_x;
    this.res_acc_y = res_acc_y;
    this.res_acc_z = res_acc_z;
    this.res_gyro_x = res_gyro_x;
    this.res_gyro_y = res_gyro_y;
    this.res_gyro_z = res_gyro_z;
    this.thingy_acc_x = thingy_acc_x;
    this.thingy_acc_y = thingy_acc_y;
    this.thingy_acc_z = thingy_acc_z;
    this.dataTime = dataTime;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      LiveData liveData = (LiveData) obj;
      return ObjectsCompat.equals(getId(), liveData.getId()) &&
              ObjectsCompat.equals(getResAccX(), liveData.getResAccX()) &&
              ObjectsCompat.equals(getResAccY(), liveData.getResAccY()) &&
              ObjectsCompat.equals(getResAccZ(), liveData.getResAccZ()) &&
              ObjectsCompat.equals(getResGyroX(), liveData.getResGyroX()) &&
              ObjectsCompat.equals(getResGyroY(), liveData.getResGyroY()) &&
              ObjectsCompat.equals(getResGyroZ(), liveData.getResGyroZ()) &&
              ObjectsCompat.equals(getThingyAccX(), liveData.getThingyAccX()) &&
              ObjectsCompat.equals(getThingyAccY(), liveData.getThingyAccY()) &&
              ObjectsCompat.equals(getThingyAccZ(), liveData.getThingyAccZ()) &&
              ObjectsCompat.equals(getDataTime(), liveData.getDataTime()) &&
              ObjectsCompat.equals(getCreatedAt(), liveData.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), liveData.getUpdatedAt());
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
      .append(getThingyAccX())
      .append(getThingyAccY())
      .append(getThingyAccZ())
      .append(getDataTime())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("LiveData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("res_acc_x=" + String.valueOf(getResAccX()) + ", ")
      .append("res_acc_y=" + String.valueOf(getResAccY()) + ", ")
      .append("res_acc_z=" + String.valueOf(getResAccZ()) + ", ")
      .append("res_gyro_x=" + String.valueOf(getResGyroX()) + ", ")
      .append("res_gyro_y=" + String.valueOf(getResGyroY()) + ", ")
      .append("res_gyro_z=" + String.valueOf(getResGyroZ()) + ", ")
      .append("thingy_acc_x=" + String.valueOf(getThingyAccX()) + ", ")
      .append("thingy_acc_y=" + String.valueOf(getThingyAccY()) + ", ")
      .append("thingy_acc_z=" + String.valueOf(getThingyAccZ()) + ", ")
      .append("dataTime=" + String.valueOf(getDataTime()) + ", ")
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
  public static LiveData justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new LiveData(
      id,
      null,
      null,
      null,
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
      thingy_acc_x,
      thingy_acc_y,
      thingy_acc_z,
      dataTime);
  }
  public interface ResAccXStep {
    BuildStep resAccX(Double resAccX);
  }
  

  public interface BuildStep {
    LiveData build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep resAccY(Double resAccY);
    BuildStep resAccZ(Double resAccZ);
    BuildStep resGyroX(Double resGyroX);
    BuildStep resGyroY(Double resGyroY);
    BuildStep resGyroZ(Double resGyroZ);
    BuildStep thingyAccX(Double thingyAccX);
    BuildStep thingyAccY(Double thingyAccY);
    BuildStep thingyAccZ(Double thingyAccZ);
    BuildStep dataTime(Temporal.DateTime dataTime);
  }
  

  public static class Builder implements ResAccXStep, BuildStep {
    private String id;
    private Double res_acc_x;
    private Double res_acc_y;
    private Double res_acc_z;
    private Double res_gyro_x;
    private Double res_gyro_y;
    private Double res_gyro_z;
    private Double thingy_acc_x;
    private Double thingy_acc_y;
    private Double thingy_acc_z;
    private Temporal.DateTime dataTime;
    @Override
     public LiveData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new LiveData(
          id,
          res_acc_x,
          res_acc_y,
          res_acc_z,
          res_gyro_x,
          res_gyro_y,
          res_gyro_z,
          thingy_acc_x,
          thingy_acc_y,
          thingy_acc_z,
          dataTime);
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
     public BuildStep dataTime(Temporal.DateTime dataTime) {
        this.dataTime = dataTime;
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
    private CopyOfBuilder(String id, Double resAccX, Double resAccY, Double resAccZ, Double resGyroX, Double resGyroY, Double resGyroZ, Double thingyAccX, Double thingyAccY, Double thingyAccZ, Temporal.DateTime dataTime) {
      super.id(id);
      super.resAccX(resAccX)
        .resAccY(resAccY)
        .resAccZ(resAccZ)
        .resGyroX(resGyroX)
        .resGyroY(resGyroY)
        .resGyroZ(resGyroZ)
        .thingyAccX(thingyAccX)
        .thingyAccY(thingyAccY)
        .thingyAccZ(thingyAccZ)
        .dataTime(dataTime);
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
     public CopyOfBuilder dataTime(Temporal.DateTime dataTime) {
      return (CopyOfBuilder) super.dataTime(dataTime);
    }
  }
  
}
