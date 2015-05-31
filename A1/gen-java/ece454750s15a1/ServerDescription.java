/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package ece454750s15a1;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerDescription implements org.apache.thrift.TBase<ServerDescription, ServerDescription._Fields>, java.io.Serializable, Cloneable, Comparable<ServerDescription> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ServerDescription");

  private static final org.apache.thrift.protocol.TField HOST_FIELD_DESC = new org.apache.thrift.protocol.TField("host", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField PPORT_FIELD_DESC = new org.apache.thrift.protocol.TField("pport", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField MPORT_FIELD_DESC = new org.apache.thrift.protocol.TField("mport", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField NCORES_FIELD_DESC = new org.apache.thrift.protocol.TField("ncores", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("type", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField SEEDS_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("seedsList", org.apache.thrift.protocol.TType.LIST, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ServerDescriptionStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ServerDescriptionTupleSchemeFactory());
  }

  public String host; // required
  public int pport; // required
  public int mport; // required
  public int ncores; // required
  /**
   * 
   * @see ServerType
   */
  public ServerType type; // required
  public List<String> seedsList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    HOST((short)1, "host"),
    PPORT((short)2, "pport"),
    MPORT((short)3, "mport"),
    NCORES((short)4, "ncores"),
    /**
     * 
     * @see ServerType
     */
    TYPE((short)5, "type"),
    SEEDS_LIST((short)6, "seedsList");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // HOST
          return HOST;
        case 2: // PPORT
          return PPORT;
        case 3: // MPORT
          return MPORT;
        case 4: // NCORES
          return NCORES;
        case 5: // TYPE
          return TYPE;
        case 6: // SEEDS_LIST
          return SEEDS_LIST;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __PPORT_ISSET_ID = 0;
  private static final int __MPORT_ISSET_ID = 1;
  private static final int __NCORES_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.HOST, new org.apache.thrift.meta_data.FieldMetaData("host", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PPORT, new org.apache.thrift.meta_data.FieldMetaData("pport", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MPORT, new org.apache.thrift.meta_data.FieldMetaData("mport", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.NCORES, new org.apache.thrift.meta_data.FieldMetaData("ncores", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TYPE, new org.apache.thrift.meta_data.FieldMetaData("type", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, ServerType.class)));
    tmpMap.put(_Fields.SEEDS_LIST, new org.apache.thrift.meta_data.FieldMetaData("seedsList", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ServerDescription.class, metaDataMap);
  }

  public ServerDescription() {
  }

  public ServerDescription(
    String host,
    int pport,
    int mport,
    int ncores,
    ServerType type,
    List<String> seedsList)
  {
    this();
    this.host = host;
    this.pport = pport;
    setPportIsSet(true);
    this.mport = mport;
    setMportIsSet(true);
    this.ncores = ncores;
    setNcoresIsSet(true);
    this.type = type;
    this.seedsList = seedsList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ServerDescription(ServerDescription other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetHost()) {
      this.host = other.host;
    }
    this.pport = other.pport;
    this.mport = other.mport;
    this.ncores = other.ncores;
    if (other.isSetType()) {
      this.type = other.type;
    }
    if (other.isSetSeedsList()) {
      List<String> __this__seedsList = new ArrayList<String>(other.seedsList);
      this.seedsList = __this__seedsList;
    }
  }

  public ServerDescription deepCopy() {
    return new ServerDescription(this);
  }

  @Override
  public void clear() {
    this.host = null;
    setPportIsSet(false);
    this.pport = 0;
    setMportIsSet(false);
    this.mport = 0;
    setNcoresIsSet(false);
    this.ncores = 0;
    this.type = null;
    this.seedsList = null;
  }

  public String getHost() {
    return this.host;
  }

  public ServerDescription setHost(String host) {
    this.host = host;
    return this;
  }

  public void unsetHost() {
    this.host = null;
  }

  /** Returns true if field host is set (has been assigned a value) and false otherwise */
  public boolean isSetHost() {
    return this.host != null;
  }

  public void setHostIsSet(boolean value) {
    if (!value) {
      this.host = null;
    }
  }

  public int getPport() {
    return this.pport;
  }

  public ServerDescription setPport(int pport) {
    this.pport = pport;
    setPportIsSet(true);
    return this;
  }

  public void unsetPport() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PPORT_ISSET_ID);
  }

  /** Returns true if field pport is set (has been assigned a value) and false otherwise */
  public boolean isSetPport() {
    return EncodingUtils.testBit(__isset_bitfield, __PPORT_ISSET_ID);
  }

  public void setPportIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PPORT_ISSET_ID, value);
  }

  public int getMport() {
    return this.mport;
  }

  public ServerDescription setMport(int mport) {
    this.mport = mport;
    setMportIsSet(true);
    return this;
  }

  public void unsetMport() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __MPORT_ISSET_ID);
  }

  /** Returns true if field mport is set (has been assigned a value) and false otherwise */
  public boolean isSetMport() {
    return EncodingUtils.testBit(__isset_bitfield, __MPORT_ISSET_ID);
  }

  public void setMportIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __MPORT_ISSET_ID, value);
  }

  public int getNcores() {
    return this.ncores;
  }

  public ServerDescription setNcores(int ncores) {
    this.ncores = ncores;
    setNcoresIsSet(true);
    return this;
  }

  public void unsetNcores() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __NCORES_ISSET_ID);
  }

  /** Returns true if field ncores is set (has been assigned a value) and false otherwise */
  public boolean isSetNcores() {
    return EncodingUtils.testBit(__isset_bitfield, __NCORES_ISSET_ID);
  }

  public void setNcoresIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __NCORES_ISSET_ID, value);
  }

  /**
   * 
   * @see ServerType
   */
  public ServerType getType() {
    return this.type;
  }

  /**
   * 
   * @see ServerType
   */
  public ServerDescription setType(ServerType type) {
    this.type = type;
    return this;
  }

  public void unsetType() {
    this.type = null;
  }

  /** Returns true if field type is set (has been assigned a value) and false otherwise */
  public boolean isSetType() {
    return this.type != null;
  }

  public void setTypeIsSet(boolean value) {
    if (!value) {
      this.type = null;
    }
  }

  public int getSeedsListSize() {
    return (this.seedsList == null) ? 0 : this.seedsList.size();
  }

  public java.util.Iterator<String> getSeedsListIterator() {
    return (this.seedsList == null) ? null : this.seedsList.iterator();
  }

  public void addToSeedsList(String elem) {
    if (this.seedsList == null) {
      this.seedsList = new ArrayList<String>();
    }
    this.seedsList.add(elem);
  }

  public List<String> getSeedsList() {
    return this.seedsList;
  }

  public ServerDescription setSeedsList(List<String> seedsList) {
    this.seedsList = seedsList;
    return this;
  }

  public void unsetSeedsList() {
    this.seedsList = null;
  }

  /** Returns true if field seedsList is set (has been assigned a value) and false otherwise */
  public boolean isSetSeedsList() {
    return this.seedsList != null;
  }

  public void setSeedsListIsSet(boolean value) {
    if (!value) {
      this.seedsList = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case HOST:
      if (value == null) {
        unsetHost();
      } else {
        setHost((String)value);
      }
      break;

    case PPORT:
      if (value == null) {
        unsetPport();
      } else {
        setPport((Integer)value);
      }
      break;

    case MPORT:
      if (value == null) {
        unsetMport();
      } else {
        setMport((Integer)value);
      }
      break;

    case NCORES:
      if (value == null) {
        unsetNcores();
      } else {
        setNcores((Integer)value);
      }
      break;

    case TYPE:
      if (value == null) {
        unsetType();
      } else {
        setType((ServerType)value);
      }
      break;

    case SEEDS_LIST:
      if (value == null) {
        unsetSeedsList();
      } else {
        setSeedsList((List<String>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case HOST:
      return getHost();

    case PPORT:
      return Integer.valueOf(getPport());

    case MPORT:
      return Integer.valueOf(getMport());

    case NCORES:
      return Integer.valueOf(getNcores());

    case TYPE:
      return getType();

    case SEEDS_LIST:
      return getSeedsList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case HOST:
      return isSetHost();
    case PPORT:
      return isSetPport();
    case MPORT:
      return isSetMport();
    case NCORES:
      return isSetNcores();
    case TYPE:
      return isSetType();
    case SEEDS_LIST:
      return isSetSeedsList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ServerDescription)
      return this.equals((ServerDescription)that);
    return false;
  }

  public boolean equals(ServerDescription that) {
    if (that == null)
      return false;

    boolean this_present_host = true && this.isSetHost();
    boolean that_present_host = true && that.isSetHost();
    if (this_present_host || that_present_host) {
      if (!(this_present_host && that_present_host))
        return false;
      if (!this.host.equals(that.host))
        return false;
    }

    boolean this_present_pport = true;
    boolean that_present_pport = true;
    if (this_present_pport || that_present_pport) {
      if (!(this_present_pport && that_present_pport))
        return false;
      if (this.pport != that.pport)
        return false;
    }

    boolean this_present_mport = true;
    boolean that_present_mport = true;
    if (this_present_mport || that_present_mport) {
      if (!(this_present_mport && that_present_mport))
        return false;
      if (this.mport != that.mport)
        return false;
    }

    boolean this_present_ncores = true;
    boolean that_present_ncores = true;
    if (this_present_ncores || that_present_ncores) {
      if (!(this_present_ncores && that_present_ncores))
        return false;
      if (this.ncores != that.ncores)
        return false;
    }

    boolean this_present_type = true && this.isSetType();
    boolean that_present_type = true && that.isSetType();
    if (this_present_type || that_present_type) {
      if (!(this_present_type && that_present_type))
        return false;
      if (!this.type.equals(that.type))
        return false;
    }

    boolean this_present_seedsList = true && this.isSetSeedsList();
    boolean that_present_seedsList = true && that.isSetSeedsList();
    if (this_present_seedsList || that_present_seedsList) {
      if (!(this_present_seedsList && that_present_seedsList))
        return false;
      if (!this.seedsList.equals(that.seedsList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(ServerDescription other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetHost()).compareTo(other.isSetHost());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHost()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.host, other.host);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPport()).compareTo(other.isSetPport());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPport()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.pport, other.pport);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMport()).compareTo(other.isSetMport());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMport()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.mport, other.mport);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNcores()).compareTo(other.isSetNcores());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNcores()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ncores, other.ncores);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetType()).compareTo(other.isSetType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.type, other.type);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSeedsList()).compareTo(other.isSetSeedsList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSeedsList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.seedsList, other.seedsList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ServerDescription(");
    boolean first = true;

    sb.append("host:");
    if (this.host == null) {
      sb.append("null");
    } else {
      sb.append(this.host);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("pport:");
    sb.append(this.pport);
    first = false;
    if (!first) sb.append(", ");
    sb.append("mport:");
    sb.append(this.mport);
    first = false;
    if (!first) sb.append(", ");
    sb.append("ncores:");
    sb.append(this.ncores);
    first = false;
    if (!first) sb.append(", ");
    sb.append("type:");
    if (this.type == null) {
      sb.append("null");
    } else {
      sb.append(this.type);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("seedsList:");
    if (this.seedsList == null) {
      sb.append("null");
    } else {
      sb.append(this.seedsList);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ServerDescriptionStandardSchemeFactory implements SchemeFactory {
    public ServerDescriptionStandardScheme getScheme() {
      return new ServerDescriptionStandardScheme();
    }
  }

  private static class ServerDescriptionStandardScheme extends StandardScheme<ServerDescription> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ServerDescription struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // HOST
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.host = iprot.readString();
              struct.setHostIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PPORT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.pport = iprot.readI32();
              struct.setPportIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // MPORT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.mport = iprot.readI32();
              struct.setMportIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // NCORES
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.ncores = iprot.readI32();
              struct.setNcoresIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.type = ServerType.findByValue(iprot.readI32());
              struct.setTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // SEEDS_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.seedsList = new ArrayList<String>(_list0.size);
                for (int _i1 = 0; _i1 < _list0.size; ++_i1)
                {
                  String _elem2;
                  _elem2 = iprot.readString();
                  struct.seedsList.add(_elem2);
                }
                iprot.readListEnd();
              }
              struct.setSeedsListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ServerDescription struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.host != null) {
        oprot.writeFieldBegin(HOST_FIELD_DESC);
        oprot.writeString(struct.host);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(PPORT_FIELD_DESC);
      oprot.writeI32(struct.pport);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(MPORT_FIELD_DESC);
      oprot.writeI32(struct.mport);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(NCORES_FIELD_DESC);
      oprot.writeI32(struct.ncores);
      oprot.writeFieldEnd();
      if (struct.type != null) {
        oprot.writeFieldBegin(TYPE_FIELD_DESC);
        oprot.writeI32(struct.type.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.seedsList != null) {
        oprot.writeFieldBegin(SEEDS_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.seedsList.size()));
          for (String _iter3 : struct.seedsList)
          {
            oprot.writeString(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ServerDescriptionTupleSchemeFactory implements SchemeFactory {
    public ServerDescriptionTupleScheme getScheme() {
      return new ServerDescriptionTupleScheme();
    }
  }

  private static class ServerDescriptionTupleScheme extends TupleScheme<ServerDescription> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ServerDescription struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetHost()) {
        optionals.set(0);
      }
      if (struct.isSetPport()) {
        optionals.set(1);
      }
      if (struct.isSetMport()) {
        optionals.set(2);
      }
      if (struct.isSetNcores()) {
        optionals.set(3);
      }
      if (struct.isSetType()) {
        optionals.set(4);
      }
      if (struct.isSetSeedsList()) {
        optionals.set(5);
      }
      oprot.writeBitSet(optionals, 6);
      if (struct.isSetHost()) {
        oprot.writeString(struct.host);
      }
      if (struct.isSetPport()) {
        oprot.writeI32(struct.pport);
      }
      if (struct.isSetMport()) {
        oprot.writeI32(struct.mport);
      }
      if (struct.isSetNcores()) {
        oprot.writeI32(struct.ncores);
      }
      if (struct.isSetType()) {
        oprot.writeI32(struct.type.getValue());
      }
      if (struct.isSetSeedsList()) {
        {
          oprot.writeI32(struct.seedsList.size());
          for (String _iter4 : struct.seedsList)
          {
            oprot.writeString(_iter4);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ServerDescription struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(6);
      if (incoming.get(0)) {
        struct.host = iprot.readString();
        struct.setHostIsSet(true);
      }
      if (incoming.get(1)) {
        struct.pport = iprot.readI32();
        struct.setPportIsSet(true);
      }
      if (incoming.get(2)) {
        struct.mport = iprot.readI32();
        struct.setMportIsSet(true);
      }
      if (incoming.get(3)) {
        struct.ncores = iprot.readI32();
        struct.setNcoresIsSet(true);
      }
      if (incoming.get(4)) {
        struct.type = ServerType.findByValue(iprot.readI32());
        struct.setTypeIsSet(true);
      }
      if (incoming.get(5)) {
        {
          org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.seedsList = new ArrayList<String>(_list5.size);
          for (int _i6 = 0; _i6 < _list5.size; ++_i6)
          {
            String _elem7;
            _elem7 = iprot.readString();
            struct.seedsList.add(_elem7);
          }
        }
        struct.setSeedsListIsSet(true);
      }
    }
  }

}

