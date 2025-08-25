package com.example.e_commerce.model.embeded;


import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class EStepToSkinCare implements Serializable {
  
    @Column(name = "B_ID")
    private int buoiId;

    @Column(name = "RT_ID")
    private int rouTineId;

    @Column(name = "ST_ID_STEP")
    private int stepId;
    public EStepToSkinCare() {}

    public EStepToSkinCare(int stepId, int buoiId, int rouTineId) {
        this.stepId = stepId;
        this.buoiId = buoiId;
        this.rouTineId = rouTineId;
    }
    
    public int getBuoiId() {
		return buoiId;
	}

	public void setBuoiId(int buoiId) {
		this.buoiId = buoiId;
	}

	public int getRouTineId() {
		return rouTineId;
	}

	public void setRouTineId(int rouTineId) {
		this.rouTineId = rouTineId;
	}

	public int getStepId() {
		return stepId;
	}

	public void setStepId(int stepId) {
		this.stepId = stepId;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EStepToSkinCare)) return false;
        EStepToSkinCare that = (EStepToSkinCare) o;
        return Objects.equals(this.getStepId(), that.getStepId()) &&
               Objects.equals(buoiId, that.getBuoiId()) &&
               Objects.equals(rouTineId, that.getRouTineId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepId, buoiId,rouTineId);
    }
}