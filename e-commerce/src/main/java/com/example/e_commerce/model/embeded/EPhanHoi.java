package com.example.e_commerce.model.embeded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EPhanHoi implements Serializable{
	 @Column(name = "SP_ID")
	    private long spId;

	    @Column(name = "KH_ID")
	    private long khId;

	    public EPhanHoi() {}

	    public EPhanHoi(Long spId, Long phId) {
	        this.spId = spId;
	        this.khId = phId;
	    }

	    public Long getSpId() {
	        return spId;
	    }

	    public void setSpId(Long spId) {
	        this.spId = spId;
	    }

	    public Long getPkhId() {
	        return khId;
	    }

	    public void setPkhId(Long pkhId) {
	        this.khId = pkhId;
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof EPhanHoi)) return false;
	        EPhanHoi that = (EPhanHoi) o;
	        return Objects.equals(spId, that.spId) &&
	               Objects.equals(khId, that.khId);
	    }

	    @Override
	    public int hashCode() {
	        return Objects.hash(spId, khId);
	    }
}
