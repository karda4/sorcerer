package ua.karda4.sorcerer;

public class Outfit {
	public Outfit(int index, int strength) {
		this.index = index;
		this.maxStrength = strength;
		this.curStrength = strength;
	}

	int index;
	private int curStrength;
	private int maxStrength;
	
	public int getCurStrength() {
		return curStrength;
	}
	public void setCurStrength(int curStrength) {
		this.curStrength = curStrength;
		if(this.curStrength < 0) this.curStrength = 0;
	}
	
	public int getMaxStrength() {
		return maxStrength;
	}
}
