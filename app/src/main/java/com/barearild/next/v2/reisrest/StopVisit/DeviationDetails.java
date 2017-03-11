
package com.barearild.next.v2.reisrest.StopVisit;

import com.barearild.next.v2.reisrest.place.Stop;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class DeviationDetails {

    private Integer ancestor;
    private String body;
    private Boolean critical;
    private String header;
    private Integer id;
    private Boolean important;
    private Boolean intern;
    private Object internalInfo;
    private DateTime lastUpdated;
    private String lastUpdatedBy;
    private String lead;
    private Boolean planned;
    private DateTime published;
    private Boolean pushed;
    private List<Stop> stops = new ArrayList<Stop>();
    private DateTime validFrom;
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
