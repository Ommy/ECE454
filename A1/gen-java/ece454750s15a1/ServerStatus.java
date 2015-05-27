/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package ece454750s15a1;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum ServerStatus implements org.apache.thrift.TEnum {
  UNREGISTERED(0),
  REGISTERED(1),
  DOWN(2);

  private final int value;

  private ServerStatus(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static ServerStatus findByValue(int value) { 
    switch (value) {
      case 0:
        return UNREGISTERED;
      case 1:
        return REGISTERED;
      case 2:
        return DOWN;
      default:
        return null;
    }
  }
}