package com.cagst.swkroa.contact;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a PhoneNumber within the system.
 *
 * @author Craig Gaskill
 */
public final class PhoneNumber implements Serializable, Comparable<PhoneNumber> {
  private static final long serialVersionUID = 8469738041039540107L;

  private long phone_id;
  private long parent_entity_id;
  private String parent_entity_name;
  private long phone_type_cd;
  private String phone_number;
  private String phone_extension;
  private boolean primary_ind;

  // meta-data
  private boolean active_ind = true;
  private long updt_cnt;

  public long getPhoneUID() {
    return phone_id;
  }

  /* package */void setPhoneUID(final long uid) {
    this.phone_id = uid;
  }

  @JsonIgnore
  public long getParentEntityUID() {
    return parent_entity_id;
  }

  public void setParentEntityUID(final long uid) {
    this.parent_entity_id = uid;
  }

  @JsonIgnore
  public String getParentEntityName() {
    return parent_entity_name;
  }

  public void setParentEntityName(final String name) {
    this.parent_entity_name = name;
  }

  public long getPhoneTypeCD() {
    return phone_type_cd;
  }

  public void setPhoneTypeCD(final long phone_type_cd) {
    this.phone_type_cd = phone_type_cd;
  }

  public String getPhoneNumber() {
    return phone_number;
  }

  public void setPhoneNumber(final String phoneNumber) {
    if (phoneNumber != null) {
      this.phone_number = phoneNumber.replaceAll("[^0-9]", "");
    } else {
      this.phone_number = null;
    }
  }

  public String getPhoneExtension() {
    return phone_extension;
  }

  public void setPhoneExtension(final String extension) {
    this.phone_extension = extension;
  }

  public boolean isPrimary() {
    return primary_ind;
  }

  public void setPrimary(boolean primary_ind) {
    this.primary_ind = primary_ind;
  }

  public boolean isActive() {
    return active_ind;
  }

  public void setActive(final boolean active) {
    this.active_ind = active;
  }

  public long getPhoneUpdateCount() {
    return updt_cnt;
  }

  /* package */void setPhoneUpdateCount(final long updateCount) {
    this.updt_cnt = updateCount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(phone_number, phone_extension);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PhoneNumber)) {
      return false;
    }

    PhoneNumber rhs = (PhoneNumber) obj;

    return Objects.equals(phone_number, rhs.getPhoneNumber()) &&
        Objects.equals(phone_extension, rhs.getPhoneExtension());
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("number", phone_number);
    builder.append("extension", phone_extension);

    return builder.build();
  }

  @Override
  public int compareTo(final PhoneNumber rhs) {
    CompareToBuilder builder = new CompareToBuilder();
    builder.append(phone_number, rhs.getPhoneNumber());
    builder.append(phone_extension, rhs.getPhoneExtension());

    return builder.build();
  }
}
