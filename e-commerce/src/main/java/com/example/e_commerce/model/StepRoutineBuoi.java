package com.example.e_commerce.model;

import java.time.LocalTime;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;

import com.example.e_commerce.model.embeded.ESanPhamPhu;
import com.example.e_commerce.model.embeded.EStepToSkinCare;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "routine_step_buoi")
public class StepRoutineBuoi {
	@EmbeddedId
    private EStepToSkinCare  id= new EStepToSkinCare();
	

    @MapsId("buoiId")
    @ManyToOne
    @JoinColumn(name = "B_ID")
    private Buoi buoi;
    
    @MapsId("rouTineId")
    @ManyToOne
    @JoinColumn(name = "R_ID")
    private RouTine rouTine;
    
    @MapsId("stepId")
    @ManyToOne
    @JoinColumn(name = "ST_ID_STEP")
    private Step step;
    
    @Column(name="STIP")
    private String Stip;
    
    @Column(name="THOIGIAN")
    private LocalTime thoiGian;

	public EStepToSkinCare getId() {
		return id;
	}

	public void setId(EStepToSkinCare id) {
		this.id = id;
	}

	public Buoi getBuoi() {
		return buoi;
	}

	public void setBuoi(Buoi buoi) {
		this.id.setBuoiId(buoi.getId());
		this.buoi = buoi;
	}

	public RouTine getRouTine() {
		return rouTine;
	}

	public void setRouTine(RouTine rouTine) {
		this.id.setRouTineId(rouTine.getId());
		this.rouTine = rouTine;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.id.setStepId(step.getId());
		this.step = step;
	}

	public String getStip() {
		return Stip;
	}

	public void setStip(String stip) {
		Stip = stip;
	}

	public LocalTime getThoiGian() {
		return thoiGian;
	}

	public void setThoiGian(LocalTime thoiGian) {
		this.thoiGian = thoiGian;
	}
    
    
    
}
