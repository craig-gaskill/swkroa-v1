package com.cagst.swkroa.contact;

import java.io.Serializable;
import java.text.Collator;

import com.cagst.swkroa.utils.SwkroaToStringStyle;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents an EmailAddress within the system.
 *
 * @author Craig Gaskill
 */
@AutoValue
@JsonPropertyOrder({
    "emailAddressUID",
    "parentEntityUID",
    "parentEntityName",
    "emailTypeCD",
    "emailAddress",
    "primary",
    "active",
    "emailAddressUpdateCount"
})
@JsonDeserialize(builder = EmailAddress.Builder.class)
public abstract class EmailAddress implements Serializable, Comparable<EmailAddress> {
  private static final long serialVersionUID = 1L;

  @JsonProperty(value = "emailAddressUID", required = true)
  public abstract long getEmailAddressUID();

  @JsonProperty(value = "parentEntityUID", required = true)
  public abstract long getParentEntityUID();

  @JsonProperty(value = "parentEntityName", required = true)
  public abstract String getParentEntityName();

  @JsonProperty(value = "emailTypeCD", required = true)
  public abstract long getEmailTypeCD();

  @JsonProperty(value = "emailAddress", required =  true)
  public abstract String getEmailAddress();

  @JsonProperty(value = "primary", required = true)
  public abstract boolean isPrimary();

  @JsonProperty(value = "active", required = true)
  public abstract boolean isActive();

  @JsonProperty(value = "emailAddressUpdateCount", required = true)
  public abstract long getEmailAddressUpdateCount();

  @Override
  public int hashCode() {
    return getEmailAddress().hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof EmailAddress)) {
      return false;
    }

    EmailAddress rhs = (EmailAddress) obj;

    return getEmailAddress().equals(rhs.getEmailAddress());
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, SwkroaToStringStyle.SWKROA_PREFIX_STYLE);
    builder.append("email", getEmailAddress());

    return builder.build();
  }

  @Override
  public int compareTo(final EmailAddress rhs) {
    Collator collator = Collator.getInstance();
    collator.setStrength(Collator.PRIMARY);

    return collator.compare(getEmailAddress(), rhs.getEmailAddress());
  }

  /**
   * Returns a {@link Builder} with default values.
   *
   * @return A {@link Builder}
   */
  public static Builder builder() {
    return new AutoValue_EmailAddress.Builder()
        .setEmailAddressUID(0L)
        .setParentEntityUID(0L)
        .setPrimary(false)
        .setActive(true)
        .setEmailAddressUpdateCount(0L);
  }

  /**
   * Returns a {@link Builder} based upon the values from the specified {@link EmailAddress}.
   *
   * @param emailAddress
   *    The {@link EmailAddress} to base this builder off of.
   *
   * @return A {@link Builder}.
   */
  public static Builder builder(EmailAddress emailAddress) {
    return new AutoValue_EmailAddress.Builder()
        .setEmailAddressUID(emailAddress.getEmailAddressUID())
        .setParentEntityUID(emailAddress.getParentEntityUID())
        .setEmailTypeCD(emailAddress.getEmailTypeCD())
        .setParentEntityName(emailAddress.getParentEntityName())
        .setEmailAddress(emailAddress.getEmailAddress())
        .setPrimary(emailAddress.isPrimary())
        .setActive(emailAddress.isActive())
        .setEmailAddressUpdateCount(emailAddress.getEmailAddressUpdateCount());
  }

  @AutoValue.Builder
  @JsonPOJOBuilder
  public abstract static class Builder {
    @JsonProperty(value = "emailAddressUID", required = true)
    public abstract Builder setEmailAddressUID(long emailAddressUID);

    @JsonProperty(value = "parentEntityUID", required = true)
    public abstract Builder setParentEntityUID(long parentEntityUID);

    @JsonProperty(value = "parentEntityName", required = true)
    public abstract Builder setParentEntityName(String parentEntityName);

    @JsonProperty(value = "emailTypeCD", required = true)
    public abstract Builder setEmailTypeCD(long emailTypeCD);

    @JsonProperty(value = "emailAddress", required =  true)
    public abstract Builder setEmailAddress(String emailAddress);

    @JsonProperty(value = "primary", required = true)
    public abstract Builder setPrimary(boolean primary);

    @JsonProperty(value = "active", required = true)
    public abstract Builder setActive(boolean active);

    @JsonProperty(value = "emailAddressUpdateCount", required = true)
    public abstract Builder setEmailAddressUpdateCount(long updateCount);

    public abstract EmailAddress build();

    @JsonCreator
    private static Builder builder() {
      return EmailAddress.builder();
    }
  }
}
