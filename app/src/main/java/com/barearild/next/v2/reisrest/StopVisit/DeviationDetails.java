
package com.barearild.next.v2.reisrest.StopVisit;

import com.barearild.next.v2.reisrest.place.Stop;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class DeviationDetails {

    @SerializedName("ancestor")
    @Expose
    private Integer ancestor;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("critical")
    @Expose
    private Boolean critical;
    @SerializedName("header")
    @Expose
    private String header;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("important")
    @Expose
    private Boolean important;
    @SerializedName("intern")
    @Expose
    private Boolean intern;
    @SerializedName("internalInfo")
    @Expose
    private Object internalInfo;
    @SerializedName("lastUpdated")
    @Expose
    private DateTime lastUpdated;
    @SerializedName("lastUpdatedBy")
    @Expose
    private String lastUpdatedBy;
    @SerializedName("lead")
    @Expose
    private String lead;
    @SerializedName("planned")
    @Expose
    private Boolean planned;
    @SerializedName("published")
    @Expose
    private DateTime published;
    @SerializedName("pushed")
    @Expose
    private Boolean pushed;
    @SerializedName("stops")
    @Expose
    private List<Stop> stops = new ArrayList<Stop>();
    @SerializedName("validFrom")
    @Expose
    private DateTime validFrom;
    @SerializedName("validTo")
    @Expose
    private DateTime validTo;

    public Integer getAncestor() {
        return ancestor;
    }

    public String getBody() {
        return body;
    }

    public Boolean getCritical() {
        return critical;
    }

    public String getHeader() {
        return header;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getImportant() {
        return important;
    }

    public Boolean getIntern() {
        return intern;
    }

    public Object getInternalInfo() {
        return internalInfo;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public String getLead() {
        return lead;
    }

    public Boolean getPlanned() {
        return planned;
    }

    public DateTime getPublished() {
        return published;
    }

    public Boolean getPushed() {
        return pushed;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public DateTime getValidFrom() {
        return validFrom;
    }

    public DateTime getValidTo() {
        return validTo;
    }

    @Override
    public String toString() {
        return "DeviationDetails{" +
                "ancestor=" + ancestor +
                ", body='" + body + '\'' +
                ", critical=" + critical +
                ", header='" + header + '\'' +
                ", id=" + id +
                ", important=" + important +
                ", intern=" + intern +
                ", internalInfo=" + internalInfo +
                ", lastUpdated=" + lastUpdated +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", lead='" + lead + '\'' +
                ", planned=" + planned +
                ", published=" + published +
                ", pushed=" + pushed +
                ", stops=" + stops +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                '}';
    }
}
