package io.openems.edge.predictor.lstmmodel.util;

public class Cell {

	private double error;

	private double wi, wo, wz, Ri, Ro, Rz;

	public double ct, ot, zt;
	double yt;

	public double dlByDy, dlByDo, dlByDc, dlByDi, dlByDz, delI, delO, delZ, it;

	public double xt;
	public double outputDataLoc;
	

	public enum propagationType {
		FWD, BCK
	};

	public Cell(double xt, double outputData) {
		this.dlByDc = this.error = 0;
		this.wi = this.wo = this.wz = this.Ri = this.Ro = this.Rz = 1;		
		this.ct = this.ot = this.zt = 0;
		this.yt = 0;
		this.dlByDy = this.dlByDo = this.dlByDc = this.dlByDi = this.dlByDz = 0;
		this.delI = this.delO = this.delZ = 0;
		this.it = 0;
		this.xt = xt;
		this.outputDataLoc = outputData;
		
	}

	public void forwardPropogation() {
		this.it = MathUtils.sigmoid(this.wi * this.xt + this.Ri * this.yt);
		this.ot = MathUtils.sigmoid(this.wo * this.xt + this.Ro * this.yt);
		this.zt = MathUtils.tanh(this.wz * this.xt + this.Rz * this.yt);
		this.ct = this.ct + this.it * this.zt;
		this.yt = this.ot * MathUtils.tanh(this.ct);
		error = this.outputDataLoc - this.yt;
		//System.out.println(toString(propagationType.FWD));

	}

	public String toString(propagationType propgationType) {
		StringBuilder str = new StringBuilder();

		switch (propgationType) {
		case BCK:
			str.append("dlByDy : " + dlByDy + " | ");
			str.append("dlByDo : " + dlByDo + " | ");
			str.append("dlByDc : " + dlByDc + " | ");
			str.append("dlByDi : " + dlByDi + " | ");
			str.append("dlByDz : " + dlByDz + " | ");
			str.append("delI : " + delI + " | ");
			str.append("delO : " + delO + " | ");
			str.append("delZ : " + delZ + " | ");
			break;
		case FWD:
			str.append("xt :" + this.xt + " | ");
			str.append("ot :" + this.ot + " | ");
			str.append("zt :" + this.zt + " | ");
			str.append("ct :" + this.ct + " | ");
			str.append("yt :" + this.yt + " | ");
			break;

		}

		return str.toString();

	}

	public void backwardPropogation() {
		this.dlByDy = this.error;
		this.dlByDo = this.dlByDy * MathUtils.tanh(this.ct);
		this.dlByDc = this.dlByDy * this.ot * MathUtils.tanhDerivative(this.ct) + this.dlByDc;
		this.dlByDi = this.dlByDc * this.zt;
		this.dlByDz = this.dlByDc * this.it;
		this.delI = this.dlByDi * MathUtils.sigmoidDerivative(this.wi + this.Ri * this.yt);
		this.delO = this.dlByDo * MathUtils.sigmoidDerivative(this.wo + this.Ro * this.yt);
		this.delZ = this.dlByDz * MathUtils.tanhDerivative(this.wz + this.Rz * this.yt);
		//System.out.println(toString(propagationType.BCK));
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getWi() {
		return wi;
	}

	public void setWi(double wi) {
		this.wi = wi;
	}

	public double getWo() {
		return wo;
	}

	public void setWo(double wo) {
		this.wo = wo;
	}

	public double getWz() {
		return wz;
	}

	public void setWz(double wz) {
		this.wz = wz;
	}

	public double getRi() {
		return Ri;
	}

	public void setRi(double ri) {
		Ri = ri;
	}

	public double getRo() {
		return Ro;
	}

	public void setRo(double ro) {
		Ro = ro;
	}

	public double getRz() {
		return Rz;
	}

	public void setRz(double rz) {
		Rz = rz;
	}

}
