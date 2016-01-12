
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

    /**
     * 
     * @return
     *     The ancestor
     */
    public Integer getAncestor() {
        return ancestor;
    }

    /**
     * 
     * @param ancestor
     *     The ancestor
     */
    public void setAncestor(Integer ancestor) {
        this.ancestor = ancestor;
    }

    /**
     * 
     * @return
     *     The body
     */
    public String getBody() {
        return body;
    }

    /**
     * 
     * @param body
     *     The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 
     * @return
     *     The critical
     */
    public Boolean getCritical() {
        return critical;
    }

    /**
     * 
     * @param critical
     *     The critical
     */
    public void setCritical(Boolean critical) {
        this.critical = critical;
    }

    /**
     * 
     * @return
     *     The header
     */
    public String getHeader() {
        return header;
    }

    /**
     * 
     * @param header
     *     The header
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The important
     */
    public Boolean getImportant() {
        return important;
    }

    /**
     * 
     * @param important
     *     The important
     */
    public void setImportant(Boolean important) {
        this.important = important;
    }

    /**
     * 
     * @return
     *     The intern
     */
    public Boolean getIntern() {
        return intern;
    }

    /**
     * 
     * @param intern
     *     The intern
     */
    public void setIntern(Boolean intern) {
        this.intern = intern;
    }

    /**
     * 
     * @return
     *     The internalInfo
     */
    public Object getInternalInfo() {
        return internalInfo;
    }

    /**
     * 
     * @param internalInfo
     *     The internalInfo
     */
    public void setInternalInfo(Object internalInfo) {
        this.internalInfo = internalInfo;
    }

    /**
     * 
     * @return
     *     The lastUpdated
     */
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     *
     * @param lastUpdated
     *     The lastUpdated
     */
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * 
     * @return
     *     The lastUpdatedBy
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     *
     * @param lastUpdatedBy
     *     The lastUpdatedBy
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * 
     * @return
     *     The lead
     */
    public String getLead() {
        return lead;
    }

    /**
     * 
     * @param lead
     *     The lead
     */
    public void setLead(String lead) {
        this.lead = lead;
    }

    /**
     * 
     * @return
     *     The planned
     */
    public Boolean getPlanned() {
        return planned;
    }

    /**
     * 
     * @param planned
     *     The planned
     */
    public void setPlanned(Boolean planned) {
        this.planned = planned;
    }

    /**
     * 
     * @return
     *     The published
     */
    public DateTime getPublished() {
        return published;
    }

    /**
     *
     * @param published
     *     The published
     */
    public void setPublished(DateTime published) {
        this.published = published;
    }

    /**
     * 
     * @return
     *     The pushed
     */
    public Boolean getPushed() {
        return pushed;
    }

    /**
     * 
     * @param pushed
     *     The pushed
     */
    public void setPushed(Boolean pushed) {
        this.pushed = pushed;
    }

    /**
     * 
     * @return
     *     The stops
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * 
     * @param stops
     *     The stops
     */
    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    /**
     * 
     * @return
     *     The validFrom
     */
    public DateTime getValidFrom() {
        return validFrom;
    }

    /**
     *
     * @param validFrom
     *     The validFrom
     */
    public void setValidFrom(DateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * 
     * @return
     *     The validTo
     */
    public DateTime getValidTo() {
        return validTo;
    }

    /**
     *
     * @param validTo
     *     The validTo
     */
    public void setValidTo(DateTime validTo) {
        this.validTo = validTo;
    }

}
