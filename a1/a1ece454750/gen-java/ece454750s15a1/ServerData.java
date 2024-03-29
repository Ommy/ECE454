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

public class ServerData implements org.apache.thrift.TBase<ServerData, ServerData._Fields>, java.io.Serializable, Cloneable, Comparable<ServerData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ServerData");

  private static final org.apache.thrift.protocol.TField ONLINE_SERVERS_FIELD_DESC = new org.apache.thrift.protocol.TField("onlineServers", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField OFFLINE_SERVERS_FIELD_DESC = new org.apache.thrift.protocol.TField("offlineServers", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ServerDataStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ServerDataTupleSchemeFactory());
  }

  public List<ServerDescription> onlineServers; // required
  public List<ServerDescription> offlineServers; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ONLINE_SERVERS((short)1, "onlineServers"),
    OFFLINE_SERVERS((short)2, "offlineServers");

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
        case 1: // ONLINE_SERVERS
          return ONLINE_SERVERS;
        case 2: // OFFLINE_SERVERS
          return OFFLINE_SERVERS;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ONLINE_SERVERS, new org.apache.thrift.meta_data.FieldMetaData("onlineServers", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ServerDescription.class))));
    tmpMap.put(_Fields.OFFLINE_SERVERS, new org.apache.thrift.meta_data.FieldMetaData("offlineServers", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ServerDescription.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ServerData.class, metaDataMap);
  }

  public ServerData() {
  }

  public ServerData(
    List<ServerDescription> onlineServers,
    List<ServerDescription> offlineServers)
  {
    this();
    this.onlineServers = onlineServers;
    this.offlineServers = offlineServers;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ServerData(ServerData other) {
    if (other.isSetOnlineServers()) {
      List<ServerDescription> __this__onlineServers = new ArrayList<ServerDescription>(other.onlineServers.size());
      for (ServerDescription other_element : other.onlineServers) {
        __this__onlineServers.add(new ServerDescription(other_element));
      }
      this.onlineServers = __this__onlineServers;
    }
    if (other.isSetOfflineServers()) {
      List<ServerDescription> __this__offlineServers = new ArrayList<ServerDescription>(other.offlineServers.size());
      for (ServerDescription other_element : other.offlineServers) {
        __this__offlineServers.add(new ServerDescription(other_element));
      }
      this.offlineServers = __this__offlineServers;
    }
  }

  public ServerData deepCopy() {
    return new ServerData(this);
  }

  @Override
  public void clear() {
    this.onlineServers = null;
    this.offlineServers = null;
  }

  public int getOnlineServersSize() {
    return (this.onlineServers == null) ? 0 : this.onlineServers.size();
  }

  public java.util.Iterator<ServerDescription> getOnlineServersIterator() {
    return (this.onlineServers == null) ? null : this.onlineServers.iterator();
  }

  public void addToOnlineServers(ServerDescription elem) {
    if (this.onlineServers == null) {
      this.onlineServers = new ArrayList<ServerDescription>();
    }
    this.onlineServers.add(elem);
  }

  public List<ServerDescription> getOnlineServers() {
    return this.onlineServers;
  }

  public ServerData setOnlineServers(List<ServerDescription> onlineServers) {
    this.onlineServers = onlineServers;
    return this;
  }

  public void unsetOnlineServers() {
    this.onlineServers = null;
  }

  /** Returns true if field onlineServers is set (has been assigned a value) and false otherwise */
  public boolean isSetOnlineServers() {
    return this.onlineServers != null;
  }

  public void setOnlineServersIsSet(boolean value) {
    if (!value) {
      this.onlineServers = null;
    }
  }

  public int getOfflineServersSize() {
    return (this.offlineServers == null) ? 0 : this.offlineServers.size();
  }

  public java.util.Iterator<ServerDescription> getOfflineServersIterator() {
    return (this.offlineServers == null) ? null : this.offlineServers.iterator();
  }

  public void addToOfflineServers(ServerDescription elem) {
    if (this.offlineServers == null) {
      this.offlineServers = new ArrayList<ServerDescription>();
    }
    this.offlineServers.add(elem);
  }

  public List<ServerDescription> getOfflineServers() {
    return this.offlineServers;
  }

  public ServerData setOfflineServers(List<ServerDescription> offlineServers) {
    this.offlineServers = offlineServers;
    return this;
  }

  public void unsetOfflineServers() {
    this.offlineServers = null;
  }

  /** Returns true if field offlineServers is set (has been assigned a value) and false otherwise */
  public boolean isSetOfflineServers() {
    return this.offlineServers != null;
  }

  public void setOfflineServersIsSet(boolean value) {
    if (!value) {
      this.offlineServers = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ONLINE_SERVERS:
      if (value == null) {
        unsetOnlineServers();
      } else {
        setOnlineServers((List<ServerDescription>)value);
      }
      break;

    case OFFLINE_SERVERS:
      if (value == null) {
        unsetOfflineServers();
      } else {
        setOfflineServers((List<ServerDescription>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ONLINE_SERVERS:
      return getOnlineServers();

    case OFFLINE_SERVERS:
      return getOfflineServers();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ONLINE_SERVERS:
      return isSetOnlineServers();
    case OFFLINE_SERVERS:
      return isSetOfflineServers();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ServerData)
      return this.equals((ServerData)that);
    return false;
  }

  public boolean equals(ServerData that) {
    if (that == null)
      return false;

    boolean this_present_onlineServers = true && this.isSetOnlineServers();
    boolean that_present_onlineServers = true && that.isSetOnlineServers();
    if (this_present_onlineServers || that_present_onlineServers) {
      if (!(this_present_onlineServers && that_present_onlineServers))
        return false;
      if (!this.onlineServers.equals(that.onlineServers))
        return false;
    }

    boolean this_present_offlineServers = true && this.isSetOfflineServers();
    boolean that_present_offlineServers = true && that.isSetOfflineServers();
    if (this_present_offlineServers || that_present_offlineServers) {
      if (!(this_present_offlineServers && that_present_offlineServers))
        return false;
      if (!this.offlineServers.equals(that.offlineServers))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(ServerData other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetOnlineServers()).compareTo(other.isSetOnlineServers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOnlineServers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.onlineServers, other.onlineServers);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetOfflineServers()).compareTo(other.isSetOfflineServers());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOfflineServers()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.offlineServers, other.offlineServers);
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
    StringBuilder sb = new StringBuilder("ServerData(");
    boolean first = true;

    sb.append("onlineServers:");
    if (this.onlineServers == null) {
      sb.append("null");
    } else {
      sb.append(this.onlineServers);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("offlineServers:");
    if (this.offlineServers == null) {
      sb.append("null");
    } else {
      sb.append(this.offlineServers);
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ServerDataStandardSchemeFactory implements SchemeFactory {
    public ServerDataStandardScheme getScheme() {
      return new ServerDataStandardScheme();
    }
  }

  private static class ServerDataStandardScheme extends StandardScheme<ServerData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ServerData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ONLINE_SERVERS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.onlineServers = new ArrayList<ServerDescription>(_list8.size);
                for (int _i9 = 0; _i9 < _list8.size; ++_i9)
                {
                  ServerDescription _elem10;
                  _elem10 = new ServerDescription();
                  _elem10.read(iprot);
                  struct.onlineServers.add(_elem10);
                }
                iprot.readListEnd();
              }
              struct.setOnlineServersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // OFFLINE_SERVERS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list11 = iprot.readListBegin();
                struct.offlineServers = new ArrayList<ServerDescription>(_list11.size);
                for (int _i12 = 0; _i12 < _list11.size; ++_i12)
                {
                  ServerDescription _elem13;
                  _elem13 = new ServerDescription();
                  _elem13.read(iprot);
                  struct.offlineServers.add(_elem13);
                }
                iprot.readListEnd();
              }
              struct.setOfflineServersIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, ServerData struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.onlineServers != null) {
        oprot.writeFieldBegin(ONLINE_SERVERS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.onlineServers.size()));
          for (ServerDescription _iter14 : struct.onlineServers)
          {
            _iter14.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.offlineServers != null) {
        oprot.writeFieldBegin(OFFLINE_SERVERS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.offlineServers.size()));
          for (ServerDescription _iter15 : struct.offlineServers)
          {
            _iter15.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ServerDataTupleSchemeFactory implements SchemeFactory {
    public ServerDataTupleScheme getScheme() {
      return new ServerDataTupleScheme();
    }
  }

  private static class ServerDataTupleScheme extends TupleScheme<ServerData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ServerData struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetOnlineServers()) {
        optionals.set(0);
      }
      if (struct.isSetOfflineServers()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetOnlineServers()) {
        {
          oprot.writeI32(struct.onlineServers.size());
          for (ServerDescription _iter16 : struct.onlineServers)
          {
            _iter16.write(oprot);
          }
        }
      }
      if (struct.isSetOfflineServers()) {
        {
          oprot.writeI32(struct.offlineServers.size());
          for (ServerDescription _iter17 : struct.offlineServers)
          {
            _iter17.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ServerData struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list18 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.onlineServers = new ArrayList<ServerDescription>(_list18.size);
          for (int _i19 = 0; _i19 < _list18.size; ++_i19)
          {
            ServerDescription _elem20;
            _elem20 = new ServerDescription();
            _elem20.read(iprot);
            struct.onlineServers.add(_elem20);
          }
        }
        struct.setOnlineServersIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list21 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.offlineServers = new ArrayList<ServerDescription>(_list21.size);
          for (int _i22 = 0; _i22 < _list21.size; ++_i22)
          {
            ServerDescription _elem23;
            _elem23 = new ServerDescription();
            _elem23.read(iprot);
            struct.offlineServers.add(_elem23);
          }
        }
        struct.setOfflineServersIsSet(true);
      }
    }
  }

}

